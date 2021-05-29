package com.digitaltaxusa.framework.http.request

/**
 * Represents different types of HTTP request bodies.
 */
sealed class RequestPayload {

    /**
     * Represents a simple json string request body.
     *
     * @property contentType The Content-Type entity header is used to indicate the
     * media type of the resource.
     * @property value The json string for the HTTP request
     */
    class StringRequestPayload(val contentType: String?, val value: String?) : RequestPayload()

    /**
     * Represents a simple form request body.
     *
     * @property queryParameters The form dictionary for the HTTP request.
     */
    class UrlQueryParameters(val queryParameters: Map<String, String>?) : RequestPayload()

    /**
     * Represents an HTTP request with a body.
     */
    object EmptyRequestPayload : RequestPayload()

    companion object {
        const val CONTENT_TYPE_APPLICATION_JSON = "application/json; charset=utf-8"
    }
}