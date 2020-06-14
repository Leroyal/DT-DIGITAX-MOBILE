package com.digitaltaxusa.framework.http.response

/**
 * Distinguishes between a runtime error and a failed HTTP response.
 *
 * @property exception The error incurred while making the HTTP request.
 */
sealed class ErrorItem(val exception: Exception) {

    /**
     * Represents an HTTP error response.
     *
     * @property httpStatusCode Response status code
     * @property responseTimeInMillis Response time in milli secs to complete the response
     * @property exception A bad HTTP request error
     */
    class HttpErrorItem(
        val httpStatusCode: HttpStatusCode,
        val responseTimeInMillis: Long? = null,
        exception: Exception
    ) : ErrorItem(exception)

    /**
     * Represents a generic runtime error.
     *
     * @property exception The runtime error that caused the HTTP request to fail.
     */
    class GenericErrorItem(exception: Exception) : ErrorItem(exception)
}