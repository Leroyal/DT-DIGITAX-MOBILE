package com.digitaltaxusa.digitax.api.requests

import com.digitaltaxusa.digitax.api.client.DigitaxApiClient

private const val DEVICE_TYPE_REQUIRED = "deviceType must be set for a proper request to be formed."
private const val EMAIL_REQUIRED = "email must be set for a proper request to be formed."
private const val PASSWORD_REQUIRED = "password must be set for a proper request to be formed."
private const val USERNAME_REQUIRED = "username must be set for a proper request to be formed."

/**
 * Request object required for performing /api/auth/signin request
 * with [DigitaxApiClient]
 *
 * <p>http://digitaxapi-env.eba-nrr834zb.us-east-1.elasticbeanstalk.com:8080/swagger-ui.html#/AuthController/authenticateUserUsingPOST</p>
 *
 * @property deviceType String The type of device used for the API call
 * @property email String Email required for /api/auth/signin request
 * @property password String Password required for /api/auth/signin request
 * @property username String Username required for /api/auth/signin request
 * @constructor
 */
class SigninRequest private constructor(builder: Builder) {

    // request properties
    private val deviceType: String
    private val email: String
    private val password: String
    private val username: String

    init {
        this.deviceType = builder.deviceType
            ?: throw IllegalStateException(DEVICE_TYPE_REQUIRED)
        this.email = builder.email
            ?: throw IllegalStateException(EMAIL_REQUIRED)
        this.password = builder.password
            ?: throw IllegalStateException(PASSWORD_REQUIRED)
        this.username = builder.username
            ?: throw IllegalStateException(USERNAME_REQUIRED)
    }

    /**
     * Embedded builder class used to simplify /api/auth/signin request object creation.
     *
     * @property deviceType String? The type of device used for the API call
     * @property email String? Email required for /api/auth/signin request
     * @property password String? Password required for /api/auth/signin request
     * @property username String? Username required for /api/auth/signin request
     */
    open class Builder {
        // request values
        var deviceType: String? = null
            private set
        var email: String? = null
            private set
        var password: String? = null
            private set
        var username: String? = null
            private set

        /**
         * Setter for setting deviceType.
         *
         * @param deviceType String REQUIRED: The type of device used for the API call
         * @return [Builder]
         */
        fun setDeviceType(deviceType: String): Builder = apply {
            this.deviceType = deviceType
        }

        /**
         * Setter for setting email.
         *
         * @param email String REQUIRED: Email required for /api/auth/signin request
         * @return [Builder]
         */
        fun setEmail(email: String): Builder = apply {
            this.email = email
        }

        /**
         * Setter for setting password.
         *
         * @param password String REQUIRED: Password required for /api/auth/signin request
         * @return [Builder]
         */
        fun setPassword(password: String): Builder = apply {
            this.password = password
        }

        /**
         * Setter for setting username.
         *
         * @param username String REQUIRED: Username required for /api/auth/signin request
         * @return [Builder]
         */
        fun setUsername(username: String): Builder = apply {
            this.username = username
        }

        @Throws(IllegalStateException::class)
        fun create(): SigninRequest {
            return SigninRequest(this)
        }
    }
}