package com.digitaltaxusa.digitax.api.client

import android.content.Context
import com.digitaltaxusa.digitax.api.configuration.DigitaxClientConfiguration
import com.digitaltaxusa.digitax.api.requests.ForgotPasswordRequest
import com.digitaltaxusa.digitax.api.requests.SigninRequest
import com.digitaltaxusa.digitax.api.requests.SignupRequest
import com.digitaltaxusa.digitax.api.response.ForgotPasswordResponse
import com.digitaltaxusa.digitax.api.response.SigninResponse
import com.digitaltaxusa.digitax.api.response.SignupResponse
import com.digitaltaxusa.framework.http.BaseApiClient

open class DigitaxApiClient(
    private val context: Context,
    internal var digitaxClientConfiguration: DigitaxClientConfiguration
) : BaseApiClient<DigitaxClientConfiguration>(digitaxClientConfiguration), DigitaxApiInterface {




    /**
     * Make request to /api/auth/signin endpoint
     *
     * @param request SigninRequest REQUIRED: Provided request model for making request.
     * @param digitaxResponseCallback DigitaxResponseCallback<SigninResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun signin(
        request: SigninRequest,
        digitaxResponseCallback: DigitaxResponseCallback<SigninResponse>
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Make request to /api/auth/signup endpoint
     *
     * @param request SignupRequest REQUIRED: Provided request model for making request.
     * @param digitaxResponseCallback DigitaxResponseCallback<SignupResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun signup(
        request: SignupRequest,
        digitaxResponseCallback: DigitaxResponseCallback<SignupResponse>
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Make request to /api/auth/forgot-password-request
     *
     * @param request ForgotPasswordRequest REQUIRED: Provided request model for making request.
     * @param digitaxResponseCallback DigitaxResponseCallback<ForgotPasswordResponse> Callback used
     * for calls which do not return data and only indicate success or failure.
     */
    override fun forgetPassword(
        request: ForgotPasswordRequest,
        digitaxResponseCallback: DigitaxResponseCallback<ForgotPasswordResponse>
    ) {
        TODO("Not yet implemented")
    }

}