package com.digitaltaxusa.digitax.api.client

import com.digitaltaxusa.framework.http.response.EmptyStateInfo
import com.digitaltaxusa.framework.http.response.Response

interface DigitaxResponseCallback<T : EmptyStateInfo> {

    /**
     * Represents that a request concluded successfully.
     *
     * @param digitaxResponse Success<T> A successful variant of the [Response]
     */
    fun onSuccess(digitaxResponse: Response.Success<T>)

    /**
     * Represents that a request failed.
     *
     * @param digitaxFailure Failure<T> A failed variant of the [Response]
     */
    fun onFailure(digitaxFailure: Response.Failure<T>)
}