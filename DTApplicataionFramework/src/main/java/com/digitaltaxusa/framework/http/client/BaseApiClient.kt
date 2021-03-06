package com.digitaltaxusa.framework.http.client

import android.os.Handler
import android.os.Looper
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.http.configuration.BaseClientConfiguration
import com.digitaltaxusa.framework.http.listeners.HttpRequestExecutor
import com.digitaltaxusa.framework.http.listeners.HttpResponseCallback
import com.digitaltaxusa.framework.http.logger.LoggerInterceptor
import com.digitaltaxusa.framework.http.okhttp.OkHttpRequestExecutor
import com.digitaltaxusa.framework.http.response.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

/**
 * This abstract class encapsulates HTTP logic.
 *
 * @param T : BaseClientConfiguration Used to abstract the attributes used in HTTP request in.
 * @property clientConfiguration T This property contains necessary info to make the HTTP requests.
 * @property firebaseAnalyticsManager Analytics manager used for logging events in Firebase.
 * @property okHttpRequestExecutor HttpRequestExecutor This interface establishes a common
 * contract for hiding HTTP library dependencies.
 * @property handler Handler Class used to run a message loop for a thread.
 * @property gson Gson This is the main class for using Gson.
 * @constructor
 */
abstract class BaseApiClient<T : BaseClientConfiguration>(
    private var clientConfiguration: T,
    protected var firebaseAnalyticsManager: FirebaseAnalyticsManager? = null,
    protected val okHttpRequestExecutor: HttpRequestExecutor = OkHttpRequestExecutor(interceptor = LoggerInterceptor()),
    private val handler: Handler = Handler(Looper.getMainLooper()),
    protected val gson: Gson = Gson()
) {

    /**
     * FOR TESTING ONLY
     */
    internal var postRunnableHook: () -> Unit = {}

    /**
     * FOR TESTING ONLY
     */
    protected fun updatePostRunnableHook(postRunnableHook: () -> Unit) {
        this.postRunnableHook = postRunnableHook
    }

    /**
     * Listen for the [HttpResponseCallback] and update [ResponseCallback] accordingly.
     *
     * @param T Generic type parameter.
     * @param identifier Description text or label for the HTTP request.
     * @param emptyResponse Empty object for [T].
     * @param responseCallback Callback to notify call-site of `onSuccess` and `onFailure` events.
     */
    inline fun <reified T : EmptyStateInfo> getHttpResponseCallback(
        identifier: String? = null,
        emptyResponse: T,
        responseCallback: ResponseCallback<T>?
    ) = object : HttpResponseCallback {
        override fun onSuccess(responseItem: ResponseItem) {
            handleValidHttpResponse(
                identifier,
                responseItem,
                emptyResponse,
                responseCallback,
                T::class.java
            )
        }

        override fun onFailure(errorItem: ErrorItem) {
            handleHttpResponseFailure(identifier, errorItem, responseCallback)
        }

        override fun onCancelled() {
            // no-op
        }
    }

    /**
     * Handle callbacks for [ResponseCallback] when a HTTP request concludes successfully.
     *
     * @param T Generic type parameter.
     * @param identifier Description text or label for the HTTP request.
     * @param responseItem HTTP response item.
     * @param emptyResponse Empty object for [T].
     * @param responseCallback Callback to notify call-site of `onSuccess` and `onFailure` events.
     */
    fun <T : EmptyStateInfo> handleValidHttpResponse(
        identifier: String? = null,
        responseItem: ResponseItem,
        emptyResponse: T,
        responseCallback: ResponseCallback<T>?,
        tClass: Class<T>
    ) {
        when (responseItem) {
            is ResponseItem.StringResponseItem -> {
                try {
                    val responseData = gson.fromJson(responseItem.response, tClass)
                    handleResponseSuccess(
                        identifier,
                        responseItem.statusCode,
                        responseData,
                        responseCallback
                    )
                } catch (e: JsonSyntaxException) {
                    handleNonHttpFailure(identifier, e, responseCallback)
                }
            }
            is ResponseItem.EmptyResponseItem -> {
                handleResponseSuccess(
                    identifier,
                    responseItem.statusCode,
                    emptyResponse,
                    responseCallback
                )
            }
        }
    }

    /**
     * Handle callbacks for [ResponseCallback] when a response succeeds.
     *
     * @param T Generic type parameter.
     * @param identifier Description text or label for the HTTP request.
     * @param httpStatusCode Represents an HTTP status with code and message.
     * @param responseData Response data.
     * @param responseCallback Callback to notify call-site of `onSuccess` and `onFailure` events.
     */
    private fun <T : EmptyStateInfo> handleResponseSuccess(
        identifier: String? = null,
        httpStatusCode: HttpStatusCode,
        responseData: T,
        responseCallback: ResponseCallback<T>?
    ) = notifyWithHandler {
        responseCallback?.onSuccess(Response.Success(httpStatusCode, responseData, identifier))
    }

    /**
     * Handle callbacks for [ResponseCallback] when a response fails.
     *
     * @param T Generic type parameter.
     * @param identifier Description text or label for the HTTP request.
     * @param errorItem Distinguishes between a runtime error and a failed HTTP response.
     * @param responseCallback Callback to notify call-site of `onSuccess` and `onFailure` events.
     */
    private fun <T : EmptyStateInfo> handleResponseFailure(
        identifier: String? = null,
        errorItem: ErrorItem,
        responseCallback: ResponseCallback<T>?
    ) = notifyWithHandler {
        responseCallback?.onFailure(Response.Failure(errorItem, identifier))
    }

    /**
     * Handle Firebase error logging for non Http failures and callbacks for failures.
     *
     * @param T Generic type parameter.
     * @param identifier Description text or label for the HTTP request.
     * @param exception An object that wraps an error event that occurred and contains information
     * about the error including its type.
     * @param responseCallback Callback to notify call-site of `onSuccess` and `onFailure` events.
     */
    private fun <T : EmptyStateInfo> handleNonHttpFailure(
        identifier: String? = null,
        exception: Exception,
        responseCallback: ResponseCallback<T>?
    ) {
        // track error
        val exceptionItem = ErrorItem.GenericErrorItem(exception)
        firebaseAnalyticsManager?.logNonApiException(exceptionItem)
        handleResponseFailure(identifier, exceptionItem, responseCallback)
    }

    /**
     * Handle New Relic error logging for Http failures and callbacks for failures.
     *
     * @param T Generic type parameter.
     * @param identifier Description text or label for the HTTP request.
     * @param errorItem Distinguishes between a runtime error and a failed HTTP response.
     * @param responseCallback Callback to notify call-site of `onSuccess` and `onFailure` events.
     */
    fun <T : EmptyStateInfo> handleHttpResponseFailure(
        identifier: String? = null,
        errorItem: ErrorItem,
        responseCallback: ResponseCallback<T>?
    ) {
        // track error
        firebaseAnalyticsManager?.logApiException(identifier, errorItem)
        handleResponseFailure(identifier, errorItem, responseCallback)
    }

    /**
     * Wrap [action] around [Handler]'s post call.
     */
    protected fun notifyWithHandler(action: () -> Unit) = handler.post { action() }
        .also { postRunnableHook() }

}