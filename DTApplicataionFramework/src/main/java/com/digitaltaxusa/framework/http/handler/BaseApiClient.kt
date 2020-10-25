package com.digitaltaxusa.framework.http.handler

import android.os.Handler
import android.os.Looper
import com.digitaltaxusa.framework.http.configuration.BaseClientConfiguration
import com.digitaltaxusa.framework.http.listeners.HttpResponseCallback
import com.digitaltaxusa.framework.http.logger.LoggerInterceptor
import com.digitaltaxusa.framework.http.okhttp.OkHttpRequestExecutor
import com.digitaltaxusa.framework.http.response.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

abstract class BaseApiClient<T : BaseClientConfiguration>(
    protected var clientConfiguration: T,
    protected val okHttpRequestExecutor: OkHttpRequestExecutor = OkHttpRequestExecutor(interceptor = LoggerInterceptor()),
    protected val handler: Handler = Handler(Looper.getMainLooper()),
    protected var gson: Gson = Gson()
) {

    /**
     * FOR TESTING ONLY
     */
    protected var postRunnableHook: () -> Unit = {}

    /**
     * Listen for the [HttpResponseCallback] and update [ResponseCallback] accordingly.
     *
     * @param T Generic type parameter
     * @param emptyResponse Empty object for [T]
     * @param foxKitResponseCallback Callback to notify call-site of `onSuccess` and `onFailure` events
     * @param identifier description text or label for the HTTP request
     * @param tClass Generic request class
     */
    protected fun <T : EmptyStateInfo> getHttpResponseCallback(
        emptyResponse: T,
        foxKitResponseCallback: ResponseCallback<T>?,
        identifier: String? = null,
        tClass: Class<T>
    ) =
        object : HttpResponseCallback {
            override fun onSuccess(response: ResponseItem) {
                handleValidHttpResponse(response, emptyResponse, foxKitResponseCallback, tClass, identifier)
            }

            override fun onFailure(httpErrorItem: ErrorItem) {
                handleHttpResponseFailure(httpErrorItem, foxKitResponseCallback, identifier)
            }

            override fun onCancelled() {
                // no-op
            }
        }

    /**
     * Handle callbacks for [ResponseCallback] when a HTTP request concludes successfully.
     *
     * @param T Generic type parameter
     * @param responseItem HTTP response item
     * @param emptyResponse Empty object for [T]
     * @param foxKitResponseCallback Callback to notify call-site of `onSuccess` and `onFailure` events
     * @param identifier description text or label for the HTTP request
     */
    protected fun <T : EmptyStateInfo> handleValidHttpResponse(
        responseItem: ResponseItem,
        emptyResponse: T,
        foxKitResponseCallback: ResponseCallback<T>?,
        tClass: Class<T>,
        identifier: String? = null
    ) {
        when (responseItem) {
            is ResponseItem.StringResponseItem -> {
                try {
                    val responseData = gson.fromJson(responseItem.response, tClass)
                    handleResponseSuccess(responseItem.statusCode, responseData, foxKitResponseCallback, identifier)
                } catch (e: JsonSyntaxException) {
                    handleNonHttpFailure(e, foxKitResponseCallback, identifier)
                }
            }
            is ResponseItem.EmptyResponseItem -> {
                handleResponseSuccess(responseItem.statusCode, emptyResponse, foxKitResponseCallback, identifier)
            }
        }
    }

    /**
     * Handle callbacks for [ResponseCallback] when a response succeeds.
     *
     * @param T Generic type parameter
     * @param httpStatusCode Represents an HTTP status with code and message.
     * @param responseData Response data
     * @param foxKitResponseCallback Callback to notify call-site of `onSuccess` and `onFailure` events
     * @param identifier description text or label for the HTTP request
     */
    protected fun <T : EmptyStateInfo> handleResponseSuccess(
        httpStatusCode: HttpStatusCode,
        responseData: T,
        foxKitResponseCallback: ResponseCallback<T>?,
        identifier: String? = null
    ) = notifyWithHandler {
        foxKitResponseCallback?.onSuccess(Response.Success(httpStatusCode, responseData, identifier))
    }

    /**
     * Handle callbacks for [ResponseCallback] when a response fails.
     *
     * @param T Generic type parameter
     * @param foxKitErrorItem Distinguishes between a runtime error and a failed HTTP response
     * @param foxKitResponseCallback Callback to notify call-site of `onSuccess` and `onFailure` events
     * @param identifier description text or label for the HTTP request
     */
    protected fun <T : EmptyStateInfo> handleResponseFailure(
        foxKitErrorItem: ErrorItem,
        foxKitResponseCallback: ResponseCallback<T>?,
        identifier: String? = null
    ) = notifyWithHandler {
        foxKitResponseCallback?.onFailure(Response.Failure(foxKitErrorItem, identifier))
    }

    /**
     * Handle New Relic error logging for non Http failures and callbacks for failures.
     *
     * @param T Generic type parameter
     * @param exception An object that wraps an error event that occurred and contains information
     * about the error including its type
     * @param foxKitResponseCallback Callback to notify call-site of `onSuccess` and `onFailure` events
     * @param identifier description text or label for the HTTP request
     */
    protected fun <T : EmptyStateInfo> handleNonHttpFailure(
        exception: Exception,
        foxKitResponseCallback: ResponseCallback<T>?,
        identifier: String? = null
    ) {
        val exceptionItem = ErrorItem.GenericErrorItem(exception)
        handleResponseFailure(exceptionItem, foxKitResponseCallback, identifier)
    }

    /**
     * Handle New Relic error logging for Http failures and callbacks for failures.
     *
     * @param T Generic type parameter
     * @param foxKitErrorItem Distinguishes between a runtime error and a failed HTTP response
     * @param foxKitResponseCallback Callback to notify call-site of `onSuccess` and `onFailure` events
     * @param identifier description text or label for the HTTP request
     */
    protected fun <T : EmptyStateInfo> handleHttpResponseFailure(
        foxKitErrorItem: ErrorItem,
        foxKitResponseCallback: ResponseCallback<T>?,
        identifier: String? = null
    ) {
        handleResponseFailure(foxKitErrorItem, foxKitResponseCallback, identifier)
    }

    /**
     * Wrap [action] around [Handler]'s post call.
     */
    protected fun notifyWithHandler(action: () -> Unit) = handler.post { action() }
        .also { postRunnableHook() }
}
