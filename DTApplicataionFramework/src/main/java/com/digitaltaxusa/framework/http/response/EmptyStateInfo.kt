package com.digitaltaxusa.framework.http.response

/**
 * Provide [isEmpty] metadata about the [Success] object.
 */
interface EmptyStateInfo {

    /**
     * Returns `true` if [Success.response] is an empty response object.
     */
    fun isEmpty(): Boolean
}