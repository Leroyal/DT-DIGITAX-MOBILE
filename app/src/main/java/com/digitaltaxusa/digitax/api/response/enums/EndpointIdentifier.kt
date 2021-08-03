package com.digitaltaxusa.digitax.api.response.enums

import com.digitaltaxusa.framework.http.response.Response

/**
 * Enumeration to add request identifier to [Response.Success] and [Response.Failure].
 *
 * @property requestName String Request name intended to be used as identifier in
 * [Response.Success] and [Response.Failure].
 * @constructor
 */
enum class EndpointIdentifier(private val requestName: String) {
    SIGNIN("Signin"),
    SIGNUP("Signup"),
    FORGOT_PASSWORD("ForgotPassword");

    override fun toString(): String {
        return requestName
    }
}