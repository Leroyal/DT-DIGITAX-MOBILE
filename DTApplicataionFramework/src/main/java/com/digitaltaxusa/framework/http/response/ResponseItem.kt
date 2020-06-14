package com.digitaltaxusa.framework.http.response

/**
 * Intended to act as a layer of abstraction over network responses, [ResponseItem] defines different types HTTP responses.
 *
 * @property statusCode HTTP response status code
 */
sealed class ResponseItem(
    val statusCode: HttpStatusCode
) {

    /**
     * Represents a HTTP string response body.
     *
     * @property response The HTTP response body
     */
    class StringResponseItem(
        statusCode: HttpStatusCode,
        val response: String?,
        val headers: Map<String, String> = emptyMap()
    ) : ResponseItem(statusCode)

    /**
     * Represents an empty HTTP response.
     */
    class EmptyResponseItem(
        statusCode: HttpStatusCode,
        val headers: Map<String, String> = emptyMap()
    ) : ResponseItem(statusCode)
}