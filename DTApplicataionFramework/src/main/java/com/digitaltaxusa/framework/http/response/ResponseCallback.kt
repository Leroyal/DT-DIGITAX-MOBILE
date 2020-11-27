package com.digitaltaxusa.framework.http.response

/**
 * Response callback for requests. Each request could finish in the following state:
 * - `onSuccess`: a request has successfully completed with [Response.Success]
 * - `onFailure`: a request failed with [Response.Failure]
 * - `onCancelled`: a request was cancelled
 *
 * @param T : EmptyStateInfo Provide [isEmpty] metadata about the [Success] object.
 */
interface ResponseCallback<T : EmptyStateInfo> {

    /**
     * Represents that a request concluded successfully.
     *
     * @param response A successful variant of the [Response].
     */
    fun onSuccess(response: Response.Success<T>)

    /**
     * Represents that a request failed.
     *
     * @param failure A failed variant of the [Response].
     */
    fun onFailure(failure: Response.Failure<T>)
}

/**
 * Denotes [ResponseCallback.onSuccess] as an alias
 */
typealias OnSuccess<T> = (Response.Success<T>) -> Unit

/**
 * Denotes [ResponseCallback.onFailure] as an alias
 */
typealias OnFailure<T> = (Response.Failure<T>) -> Unit