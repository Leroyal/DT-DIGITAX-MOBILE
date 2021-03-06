package com.digitaltaxusa.framework.http.okhttp

import androidx.annotation.VisibleForTesting
import com.digitaltaxusa.framework.http.listeners.HttpRequestExecutor
import com.digitaltaxusa.framework.http.listeners.HttpResponseCallback
import com.digitaltaxusa.framework.http.okhttp.internal.RequestCleanupSpec
import com.digitaltaxusa.framework.http.okhttp.internal.RequestCleanupStrategy
import com.digitaltaxusa.framework.http.okhttp.internal.RequestState
import com.digitaltaxusa.framework.http.request.HttpException
import com.digitaltaxusa.framework.http.request.HttpMethod
import com.digitaltaxusa.framework.http.request.HttpRequest
import com.digitaltaxusa.framework.http.request.RequestPayload
import com.digitaltaxusa.framework.http.response.ErrorItem
import com.digitaltaxusa.framework.http.response.HttpStatusCode
import com.digitaltaxusa.framework.http.response.ResponseItem
import com.digitaltaxusa.framework.logger.Logger
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.Closeable
import java.io.IOException

internal const val TAG = "OkHttpRequestExecutor"
internal const val ERROR_MESSAGE_NULL_EMPTY_URL = "Url cannot be null or empty"
internal val EMPTY_BYTE_ARRAY = ByteArray(0)
internal val EMPTY_REQUEST = EMPTY_BYTE_ARRAY.toRequestBody(null, 0, 0)

/**
 * HTTP request executor implementation for [OkHttpClient].
 *
 * @param client Factory for calls, which can be used to send HTTP requests and read their responses.
 * @param interceptor Observes, modifies, and potentially short-circuits requests going out and the
 * corresponding responses coming back in. Typically interceptors add, remove, or transform headers
 * on the request or response.
 * @param connectionSpec Unencrypted, unauthenticated connections for http: URLs.
 */
open class OkHttpRequestExecutor(
    private var client: OkHttpClient = OkHttpClient(),
    private var interceptor: Interceptor? = null,
    private var connectionSpec: List<ConnectionSpec> = emptyList(),
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val requestCleanupStrategy: RequestCleanupSpec = RequestCleanupStrategy()
) : HttpRequestExecutor {

    override fun execute(
        httpRequest: HttpRequest,
        callback: HttpResponseCallback?
    ) {
        requestCleanupStrategy.onStateChanged(RequestState.Ongoing)
        requestCleanupStrategy.callback = callback

        // establish pre-checks
        if (httpRequest.url.isNullOrEmpty()) {
            callback?.onFailure(
                ErrorItem.GenericErrorItem(
                    NullPointerException(
                        ERROR_MESSAGE_NULL_EMPTY_URL
                    )
                )
            )
            requestCleanupStrategy.onStateChanged(RequestState.Failed)
            return
        }

        // build (instantiate) client configuration with consideration of custom properties
        buildClientConfiguration()

        // initialize request
        val okHttpRequest = buildRequest(
            httpRequest.url,
            httpRequest.httpMethod,
            httpRequest.headers,
            httpRequest.requestPayload
        )
        val apiCall = client.newCall(okHttpRequest)

        requestCleanupStrategy.ongoingCall = apiCall

        // execute request
        apiCall.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                handleHttpRequestFailure(e, callback)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    handleSuccessfulResponse(response, callback)
                } else {
                    handleErrorResponse(response, callback)
                }
                closeQuietly(response, TAG)
            }
        })
    }

    /**
     * Cancel ongoing/last know request. Although request cancellation is not guaranteed,
     * this call will ensure if cancelled, the response callback reflects the correct state
     * along with internal resource cleanup.
     */
    override fun cancel() {
        if (requestCleanupStrategy.ongoingCall == null || requestCleanupStrategy.ongoingCall?.isCanceled() == true) {
            return
        }
        requestCleanupStrategy.ongoingCall?.cancel()
        requestCleanupStrategy.callback?.onCancelled()
        requestCleanupStrategy.onStateChanged(RequestState.Cancelled)
    }

    /**
     * Build factory for calls, which can be used to send HTTP requests and read their responses.
     *
     * <p>Configures client based on interceptor and connecton spec properties<p/>
     */
    private fun buildClientConfiguration() {
        if (connectionSpec.isNotEmpty() || interceptor != null) {
            if (connectionSpec.isNotEmpty() && interceptor != null) {
                // add interceptor
                // add connection specs
                interceptor?.run {
                    client = client.newBuilder()
                        .addInterceptor(this)
                        .connectionSpecs(connectionSpec)
                        .build()
                }
            } else if (interceptor != null) {
                // add interceptor
                interceptor?.run {
                    client = client.newBuilder()
                        .addInterceptor(this)
                        .build()
                }
            } else if (connectionSpec.isNotEmpty()) {
                // add connection specs
                client = client.newBuilder()
                    .connectionSpecs(connectionSpec)
                    .build()
            }
        }
    }

    /**
     * Build an appropriate [Request] object for the HTTP request.
     *
     * @param url The request url.
     * @param httpMethod The HTTP method.
     * @param httpHeaders The HTTP request header.
     * @param requestPayload The request payload.
     *
     * @return [Request] object for the HTTP request.
     */
    @VisibleForTesting
    fun buildRequest(
        url: String,
        httpMethod: HttpMethod,
        httpHeaders: Map<String, String>?,
        requestPayload: RequestPayload?
    ): Request {
        return Request.Builder().apply {
            // initialize URL
            when (val httpUrl = buildHttpUrl(url, requestPayload)) {
                null -> url(url)
                else -> url(httpUrl)
            }
            // initialize headers
            httpHeaders?.let {
                headers(it.toHeaders())
            }
            val requestBody = buildRequestBody(requestPayload)
            when (httpMethod) {
                HttpMethod.GET -> get()
                HttpMethod.POST -> post(requestBody.orEmpty())
                HttpMethod.PUT -> put(requestBody.orEmpty())
                HttpMethod.PATCH -> patch(requestBody.orEmpty())
                HttpMethod.DELETE -> requestBody?.let { delete(it) } ?: delete()
            }
        }.build()
    }

    /**
     * Build an appropriate [HttpUrl] from the [requestPayload]. Adds additional query
     * parameters to the url if necessary.
     *
     * @param url The request url.
     * @param requestPayload The request payload.
     *
     * @return A nullable [HttpUrl] with the appropriate query parameters, if necessary.
     */
    @VisibleForTesting
    fun buildHttpUrl(
        url: String,
        requestPayload: RequestPayload?
    ): HttpUrl? {
        if (requestPayload !is RequestPayload.UrlQueryParameters) {
            return url.toHttpUrlOrNull()
        }
        if (requestPayload.queryParameters.isNullOrEmpty()) {
            return url.toHttpUrlOrNull()
        }
        return url.toHttpUrlOrNull()?.newBuilder()?.apply {
            requestPayload.queryParameters.keys.forEach { key ->
                addEncodedQueryParameter(key, requestPayload.queryParameters[key])
            }
        }?.build()
    }

    /**
     * Build the HTTP request body.
     *
     * @param requestPayload The request payload.
     *
     * @return A nullable [RequestBody] for the HTTP request.
     */
    @VisibleForTesting
    fun buildRequestBody(requestPayload: RequestPayload?): RequestBody? {
        return when (requestPayload) {
            is RequestPayload.StringRequestPayload ->
                requestPayload.value.orEmpty()
                    .toRequestBody(requestPayload.contentType.orEmpty().toMediaTypeOrNull())
            is RequestPayload.EmptyRequestPayload -> EMPTY_REQUEST
            else -> null
        }
    }

    /**
     * Handle [okhttp3.Callback.onResponse] events for an HTTP request.
     *
     * This method specifically handles cases when a response successfully concludes
     * (HTTP response code is between 200 and 300).
     *
     * @param response An HTTP response. Instances of this class are not immutable: the response
     * body is a one-shot value that may be consumed only once and then closed. All other
     * properties are immutable.
     * @param callback A success/failure driven callback for HTTP response(s).
     */
    private fun handleSuccessfulResponse(
        response: Response,
        callback: HttpResponseCallback?
    ) {
        if (requestCleanupStrategy.currentRequestState == RequestState.Cancelled) {
            return
        }
        val stringBody = response.body?.string()
        val statusCode = HttpStatusCode.fromStatusCode(response.code)
        val responseHeaders = response.headers

        val headers = mutableMapOf<String, String>()
        responseHeaders.forEach { headers[it.first] = it.second }

        callback?.onSuccess(
            responseItem = if (stringBody.isNullOrEmpty()) {
                ResponseItem.EmptyResponseItem(statusCode, headers)
            } else {
                ResponseItem.StringResponseItem(statusCode, stringBody, headers)
            }
        )
        requestCleanupStrategy.onStateChanged(RequestState.Successful)
    }

    /**
     * Handle [okhttp3.Callback.onFailure] events for an HTTP request.
     *
     * @param e Signals that an I/O exception of some sort has occurred.
     * @param callback A success/failure driven callback for HTTP response(s).
     */
    private fun handleHttpRequestFailure(
        e: IOException,
        callback: HttpResponseCallback?
    ) {
        if (requestCleanupStrategy.currentRequestState == RequestState.Cancelled) {
            return
        }
        callback?.onFailure(ErrorItem.GenericErrorItem(e))
        requestCleanupStrategy.onStateChanged(RequestState.Failed)
    }

    /**
     * Handle [okhttp3.Callback.onResponse] events for an HTTP request.
     *
     * This method specifically handles cases when a response is unsuccessful
     * (HTTP response code is higher than 300).
     *
     * @param response An HTTP response. Instances of this class are not immutable: the response
     * body is a one-shot value that may be consumed only once and then closed. All other
     * properties are immutable.
     * @param callback A success/failure driven callback for HTTP response(s).
     */
    private fun handleErrorResponse(
        response: Response,
        callback: HttpResponseCallback?
    ) {
        if (requestCleanupStrategy.currentRequestState == RequestState.Cancelled) {
            return
        }
        val responseTimeInMillSecs =
            response.receivedResponseAtMillis - response.sentRequestAtMillis
        callback?.onFailure(
            ErrorItem.HttpErrorItem(
                httpStatusCode = HttpStatusCode.fromStatusCode(response.code),
                responseTimeInMillis = responseTimeInMillSecs,
                exception = HttpException(response.body?.string())
            )
        )
        requestCleanupStrategy.onStateChanged(RequestState.Failed)
    }

    /**
     * An extension function on [RequestBody] to return [EMPTY_REQUEST] if null.
     */
    private fun RequestBody?.orEmpty() = this ?: EMPTY_REQUEST

    /**
     * Method which closes a [Closeable] and absorbs [IOException] if it is thrown.
     *
     * @param closeable A Closeable is a source or destination of data that can be closed.
     * The close method is invoked to release resources that the object is holding
     * (such as open files).
     * @param tag Used to identify the source of a log message. It usually identifies the
     * class or activity where the log call occurs.
     */
    internal fun closeQuietly(
        closeable: Closeable?,
        tag: String
    ) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: IOException) {
                Logger.i(tag, "Unable to close the closeable.")
            }
        }
    }
}