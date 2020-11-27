package com.digitaltaxusa.framework.http.response

/**
 * Intended to act as a layer of abstraction over network responses, [ResponseItem] defines different types HTTP responses.
 *
 * @property statusCode HTTP response status code.
 */
sealed class ResponseItem(
    val statusCode: HttpStatusCode
) {

    /**
     * Represents a HTTP string response body.
     *
     * @property statusCode Represents an HTTP status with code and message.
     * @property response The HTTP response body.
     * @property headers The HTTP headers.
     */
    class StringResponseItem(
        statusCode: HttpStatusCode,
        val response: String?,
        val headers: Map<String, String> = emptyMap()
    ) : ResponseItem(statusCode)

    /**
     * Represents an empty HTTP response.
     *
     * @property statusCode Represents an HTTP status with code and message.
     * @property headers The HTTP headers.
     */
    class EmptyResponseItem(
        statusCode: HttpStatusCode,
        val headers: Map<String, String> = emptyMap()
    ) : ResponseItem(statusCode)
}