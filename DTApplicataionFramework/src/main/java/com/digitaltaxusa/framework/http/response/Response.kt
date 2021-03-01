package com.digitaltaxusa.framework.http.response

/**
 * Most HTTP calls will return the appropriate response object wrapped inside the [Response].
 *
 * [Response] categorizes response objects into two states.
 * - [Success]: Denotes that an API call finished successfully; note that [Success] has it's own states as well.
 * - [Failure]: An API call failed during execution; probable errors are categorized as a part of [ErrorItem].
 *
 * @param T : [EmptyStateInfo] Provide [isEmpty] metadata about the [Success] object.
 */
sealed class Response<T : EmptyStateInfo> {

    /**
     * Represents a successful response.
     *
     * @property identifier Description text or label for the HTTP request.
     * @property httpStatusCode Request status code for the API request.
     * @property response Object of the API request.
     */
    data class Success<T : EmptyStateInfo>(
        val httpStatusCode: HttpStatusCode,
        val response: T,
        val identifier: String? = null
    ) : Response<T>()

    /**
     * Represents a failed request.
     *
     * @property identifier Description text or label for the HTTP request.
     * @property exception A request error wrapped inside the [ErrorItem].
     */
    data class Failure<T : EmptyStateInfo>(
        val exception: ErrorItem,
        val identifier: String? = null
    ) : Response<T>()
}