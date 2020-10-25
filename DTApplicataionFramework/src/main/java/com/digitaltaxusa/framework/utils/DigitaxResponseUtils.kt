package com.digitaltaxusa.framework.utils

import com.digitaltaxusa.framework.http.response.EmptyStateInfo
import com.digitaltaxusa.framework.http.response.ErrorItem
import com.digitaltaxusa.framework.http.response.Response

/**
 * Return error string from [Response.Failure]
 *
 * @receiver Response.Failure<T> Represents a failed request.
 * @return String?
 */
fun <T : EmptyStateInfo> Response.Failure<T>.getErrorMessage() =
    when (val digitaxError = exception) {
        is ErrorItem.HttpErrorItem -> {
            "Got http status code: ${digitaxError.httpStatusCode} \nand exception: ${digitaxError.exception.message}"
        }
        is ErrorItem.GenericErrorItem -> {
            digitaxError.exception.message
        }
    }