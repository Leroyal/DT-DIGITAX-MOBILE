package com.digitaltaxusa.digitax.api.client

import com.digitaltaxusa.digitax.api.requests.ForgotPasswordRequest
import com.digitaltaxusa.digitax.api.requests.SigninRequest
import com.digitaltaxusa.digitax.api.requests.SignupRequest
import com.digitaltaxusa.digitax.api.response.ForgotPasswordResponse
import com.digitaltaxusa.digitax.api.response.SigninResponse
import com.digitaltaxusa.digitax.api.response.SignupResponse
import com.digitaltaxusa.framework.http.response.ResponseCallback

interface ApiInterface {

    /**
     * Make request to /api/auth/signin endpoint.
     *
     * @param request SigninRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<SigninResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    fun signin(
        request: SigninRequest,
        responseCallback: ResponseCallback<SigninResponse>
    )

    /**
     * Make request to /api/auth/signup endpoint.
     *
     * @param request SignupRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<SignupResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    fun signup(
        request: SignupRequest,
        responseCallback: ResponseCallback<SignupResponse>
    )

    /**
     * Make request to /api/auth/forgot-password-request.
     *
     * @param request ForgotPasswordRequest REQUIRED: Provided request model for making request.
     * @param responseCallback ResponseCallback<ForgotPasswordResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    fun forgetPassword(
        request: ForgotPasswordRequest,
        responseCallback: ResponseCallback<ForgotPasswordResponse>
    )

}