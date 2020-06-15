package com.digitaltaxusa.framework.http.request

/**
 * Class to map [HttpException] in [OkHttpRequestExecutor].
 *
 * @property responseBody error string of [HttpException]
 */
data class HttpException(val responseBody: String?) : Exception(responseBody)