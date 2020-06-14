package com.digitaltaxusa.framework.http

import android.os.Handler
import android.os.Looper
import com.digitaltaxusa.framework.http.configuration.BaseClientConfiguration
import com.digitaltaxusa.framework.http.listeners.HttpRequestExecutor
import com.google.gson.Gson


abstract class BaseApiClient<T: BaseClientConfiguration>(
    protected var clientConfiguration: T,
    protected val okHttpRequestExecutor: HttpRequestExecutor = OkHttpRequestExecutor(interceptor = LoggerInterceptor()),
    protected val handler: Handler = Handler(Looper.getMainLooper()),
    protected val gson: Gson = Gson()
) {

}