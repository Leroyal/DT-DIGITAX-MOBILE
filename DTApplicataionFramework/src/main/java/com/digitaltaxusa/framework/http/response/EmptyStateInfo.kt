package com.digitaltaxusa.framework.http.response

import com.digitaltaxusa.framework.http.response.Response.Success

/**
 * Provide [isEmpty] metadata about the [Success] object.
 */
interface EmptyStateInfo {

    /**
     * Returns `true` if [Response.Success.response] is an empty response object.
     */
    fun isEmpty(): Boolean
}