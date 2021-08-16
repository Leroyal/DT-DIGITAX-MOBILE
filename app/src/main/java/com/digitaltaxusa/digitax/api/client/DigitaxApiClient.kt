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
import com.digitaltaxusa.digitax.room.enums.Enums
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.http.client.BaseApiClient
import com.digitaltaxusa.framework.http.request.HttpMethod
import com.digitaltaxusa.framework.http.request.HttpRequest
import com.digitaltaxusa.framework.http.request.RequestPayload
import com.digitaltaxusa.framework.http.response.ResponseCallback

open class DigitaxApiClient(
    context: Context,
    private var clientConfiguration: DigitaxClientConfiguration
) : DigitaxApiInterface, BaseApiClient<DigitaxClientConfiguration>(
    clientConfiguration
) {

    init {
        // initialize and get instance for [FirebaseAnalyticsManager]
        firebaseAnalyticsManager = FirebaseAnalyticsManager.getInstance(context)
    }

    /**
     * Make request to /api/auth/signin endpoint.
     *
     * @param request SigninRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<SigninResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun signin(
        request: SigninRequest,
        responseCallback: ResponseCallback<SigninResponse>
    ) {
        // track api request
        val bundle = Bundle()
        bundle.putString(FirebaseAnalyticsManager.Params.KEY_REQUEST, request.toString())
        firebaseAnalyticsManager?.logEvent(FirebaseAnalyticsManager.Event.API_REQUEST, bundle)

        // compose HTTP request
        val httpRequest = HttpRequest(
            url = clientConfiguration.signinUrl,
            httpMethod = HttpMethod.POST,
            requestPayload = jsonPayload(adapt(request))
        )

        // perform POST operation
        okHttpRequestExecutor.execute(
            httpRequest, getHttpResponseCallback(
                EndpointIdentifier.SIGNIN.toString(),
                SigninResponse.EMPTY,
                responseCallback
            )
        )
    }

    /**
     * Make request to /api/auth/signup endpoint.
     *
     * @param request SignupRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<SignupResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun signup(
        request: SignupRequest,
        responseCallback: ResponseCallback<SignupResponse>
    ) {
        // track api request
        val bundle = Bundle()
        bundle.putString(FirebaseAnalyticsManager.Params.KEY_REQUEST, request.toString())
        firebaseAnalyticsManager?.logEvent(FirebaseAnalyticsManager.Event.API_REQUEST, bundle)

        // compose HTTP request
        val httpRequest = HttpRequest(
            url = clientConfiguration.signupUrl,
            httpMethod = HttpMethod.POST,
            requestPayload = jsonPayload(adapt(request))
        )

        // perform POST operation
        okHttpRequestExecutor.execute(
            httpRequest, getHttpResponseCallback(
                EndpointIdentifier.SIGNUP.toString(),
                SignupResponse.EMPTY,
                responseCallback
            )
        )
    }

    /**
     * Make request to /api/auth/forgot-password-request.
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
     * @return [RequestPayload.StringRequestPayload]
     */
    private fun <T> jsonPayload(payload: T) = RequestPayload.StringRequestPayload(
        RequestPayload.CONTENT_TYPE_APPLICATION_JSON,
        gson.toJson(payload)
    )
}