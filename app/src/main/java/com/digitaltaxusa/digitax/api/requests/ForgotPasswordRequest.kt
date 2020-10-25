package com.digitaltaxusa.digitax.api.requests

import com.digitaltaxusa.digitax.api.client.DigitaxApiClient

private const val EMAIL_REQUIRED = "Email must be set for a proper request to be formed."

/**
 * Request object required for performing /api/auth/forgot-password-request request
 * with [DigitaxApiClient]
 *
 * <p>http://digitaxapi-env.eba-nrr834zb.us-east-1.elasticbeanstalk.com:8080/swagger-ui.html#/AuthController/authenticateUserUsingPOST</p>
 *
 * @property email String Email required for /api/auth/forgot-password-request request
 * @constructor
 */
class ForgotPasswordRequest private constructor(builder: Builder) {

    // request properties
    private val email: String

    init {
        this.email = builder.email
            ?: throw IllegalStateException(EMAIL_REQUIRED)
    }

    /**
     * Embedded builder class used to simplify /api/auth/forgot-password-request request object creation.
     *
     * @property email String? Email required for /api/auth/forgot-password-request request
     */
    open class Builder {
        // request values
        var email: String? = null
            private set

        /**
         * Setter for setting email.
         *
         * @param email String REQUIRED: Email required for /api/auth/forgot-password-request request
         * @return [Builder]
         */
        fun setEmail(email: String): Builder = apply {
            this.email = email
        }

        @Throws(IllegalStateException::class)
        fun create(): ForgotPasswordRequest {
            return ForgotPasswordRequest(this)
        }
    }
}