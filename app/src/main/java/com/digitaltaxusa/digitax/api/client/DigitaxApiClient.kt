package com.digitaltaxusa.digitax.api.client

import android.content.Context
import android.os.Bundle
import com.digitaltaxusa.digitax.api.client.internal.adapter.adapt
import com.digitaltaxusa.digitax.api.configuration.DigitaxClientConfiguration
import com.digitaltaxusa.digitax.api.requests.ForgotPasswordRequest
import com.digitaltaxusa.digitax.api.requests.SigninRequest
import com.digitaltaxusa.digitax.api.requests.SignupRequest
import com.digitaltaxusa.digitax.api.response.ForgotPasswordResponse
import com.digitaltaxusa.digitax.api.response.SigninResponse
import com.digitaltaxusa.digitax.api.response.SignupResponse
import com.digitaltaxusa.digitax.api.response.enums.EndpointIdentifier
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.http.BaseApiClient
import com.digitaltaxusa.framework.http.request.HttpMethod
import com.digitaltaxusa.framework.http.request.HttpRequest
import com.digitaltaxusa.framework.http.request.RequestPayload
import com.digitaltaxusa.framework.http.response.ResponseCallback

open class DigitaxApiClient(
    context: Context,
    private var digitaxClientConfiguration: DigitaxClientConfiguration
) : DigitaxApiInterface, BaseApiClient<DigitaxClientConfiguration>(
    digitaxClientConfiguration
) {

    init {
        // initialize and get instance for [FirebaseAnalyticsManager]
        firebaseAnalyticsManager = FirebaseAnalyticsManager.getInstance(context)
    }

    /**
     * Make request to /api/auth/signin endpoint
     *
     * @param request SigninRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<SigninResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun signin(
        request: SigninRequest,
        responseCallback: ResponseCallback<SigninResponse>
    ) {
        // track user request
        val bundle = Bundle()
        bundle.putString(FirebaseAnalyticsManager.Params.REQUEST, request.toString())
        firebaseAnalyticsManager?.logEvent(FirebaseAnalyticsManager.Event.SIGN_IN, bundle)

        // compose HTTP request
        val httpRequest = HttpRequest(
            url = digitaxClientConfiguration.signinUrl,
            httpMethod = HttpMethod.POST,
            requestPayload = jsonPayload(adapt(request))
        )

        // perform POST operation
        okHttpRequestExecutor.execute(httpRequest, getHttpResponseCallback(
            EndpointIdentifier.SIGNIN.toString(),
            SigninResponse.EMPTY,
            responseCallback)
        )
    }

    /**
     * Make request to /api/auth/signup endpoint
     *
     * @param request SignupRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<SignupResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun signup(
        request: SignupRequest,
        responseCallback: ResponseCallback<SignupResponse>
    ) {
        // track user request
        val bundle = Bundle()
        bundle.putString(FirebaseAnalyticsManager.Params.REQUEST, request.toString())
        firebaseAnalyticsManager?.logEvent(FirebaseAnalyticsManager.Event.SIGN_UP, bundle)

        // compose HTTP request
        val httpRequest = HttpRequest(
            url = digitaxClientConfiguration.signupUrl,
            httpMethod = HttpMethod.POST,
            requestPayload = jsonPayload(adapt(request))
        )

        // perform POST operation
        okHttpRequestExecutor.execute(httpRequest, getHttpResponseCallback(
            EndpointIdentifier.SIGNUP.toString(),
            SignupResponse.EMPTY,
            responseCallback)
        )
    }

    /**
     * Make request to /api/auth/forgot-password-request
     *
     * @param request ForgotPasswordRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<ForgotPasswordResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun forgetPassword(
        request: ForgotPasswordRequest,
        responseCallback: ResponseCallback<ForgotPasswordResponse>
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Convert the given payload into its json representation using gson and return a RequestPayload
     * with that serialized value
     *
     * @param payload T Any payload represented by Generics.
     * @return StringRequestPayload
     */
    private fun <T> jsonPayload(payload: T) = RequestPayload.StringRequestPayload(
        RequestPayload.CONTENT_TYPE_APPLICATION_JSON,
        gson.toJson(payload)
    )
}