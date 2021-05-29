package com.digitaltaxusa.framework.http.request

/**
 * Represents all the required parameters for an HTTP request.
 *
 * @property url The request URL.
 * @property httpMethod Type of HTTP request method to be used. For e.g. [HttpMethod.GET],
 * [HttpMethod.POST], etc.
 * @property headers Additional headers to be included in the HTTP request.
 * @property requestPayload The request parameter to be included as a part of the HTTP request.
 */
class HttpRequest(
    val url: String?,
    val httpMethod: HttpMethod,
    val headers: Map<String, String>? = null,
    val requestPayload: RequestPayload? = null
)