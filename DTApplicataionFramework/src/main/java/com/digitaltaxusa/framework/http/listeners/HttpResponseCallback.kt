package com.digitaltaxusa.framework.http.listeners

import com.digitaltaxusa.framework.http.response.ErrorItem
import com.digitaltaxusa.framework.http.response.ResponseItem

/**
 * A success/failure driven callback for HTTP response(s).
 */
interface HttpResponseCallback {

    /**
     * Callback for a successful HTTP response
     *
     * @param response The response body of the HTTP request
     */
    fun onSuccess(response: ResponseItem)

    /**
     * Callback for failed HTTP response
     *
     * @param error The status code for the HTTP request
     */
    fun onFailure(error: ErrorItem)

    /**
     * Callback for a cancelled HTTP request
     */
    fun onCancelled()
}