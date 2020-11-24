package com.digitaltaxusa.digitax.api.configuration

import com.digitaltaxusa.framework.http.configuration.BaseClientConfiguration
import com.digitaltaxusa.framework.http.configuration.CommonClientConfigurationProperties

private const val ILLEGAL_ARGUMENT_EXCEPTION_BASE_URL =
    "Failed to read baseUrl. This field cannot be empty."

/**
 * This represents a configuration that collects information necessary to perform EPG
 * request operations.
 *
 * @property commonConfiguration Common configuration properties. This class prevents redundancy
 * of common configuration attributes across all FoxKit SDK modules. A configuration collects
 * information necessary to perform request operations and instantiate the FoxKit client.
 */
data class DigitaxClientConfiguration internal constructor(
    private val commonConfiguration: CommonClientConfigurationProperties?
) : BaseClientConfiguration() {

    /**
     * Debuggable state is optional, is primarily used for mocking behaviors. If in debug mode,
     * http requests are not made. Instead, static stubs are returned back as responses.
     *
     * Flag is set to false by default.
     *
     * @property debugMode OPTIONAL: Toggle for setting debuggable state.
     */
    var debugMode: Boolean
        get() = commonConfiguration?.debugMode ?: false
        set(value) {
            commonConfiguration?.debugMode = value
        }

    /**
     * Base url is required for performing requests.
     * <p>The base url is sanitized, so if a base url is set that does not include a protocol,
     * a protocol (https) will automatically be added. If a protocol already exists, no modifications
     * will occur. If you do not include the ending backslash, a backslash will be appended
     * to the end, otherwise no modifications will occur</p>
     *
     * @property baseUrl REQUIRED: Base url will be used to perform requests.
     */
    var baseUrl: String?
        get() = commonConfiguration?.baseUrl
        set(value) {
            commonConfiguration?.baseUrl = value
        }

    // build encapsulated urls
    val signinUrl: String = "$baseUrl$PATH_SIGNIN"
    val signupUrl: String = "$baseUrl$PATH_SIGNUP"
    val signoutUrl: String = "$baseUrl$PATH_SIGNOUT"
    val forgotPasswordUrl: String = "$baseUrl$PATH_FORGOT_PASSWORD"

    companion object {
        private const val PATH_SIGNIN = "/api/auth/signin"
        private const val PATH_SIGNUP = "/api/auth/signup"
        private const val PATH_SIGNOUT = "/api/auth/signout"
        private const val PATH_FORGOT_PASSWORD = "/api/auth/forgot-password-request"
    }

    /**
     * Builder pattern is a creational design pattern. It means it solves problems
     * related to object creation.
     *
     * <p>Builder pattern is used to create instance of very complex object having telescoping
     * constructor in easiest way.</p>
     *
     * @property debugMode Boolean? OPTIONAL: Toggle for setting debuggable state.
     * @property baseUrl String? REQUIRED: base url that hosts Fox's api.
     * @constructor
     */
    data class Builder(
        private var debugMode: Boolean? = false,
        private var baseUrl: String? = null
    ) {

        /**
         * Setter for setting debugMode
         *
         * @param isDebugMode OPTIONAL: Toggle for setting debuggable state.
         * @return [DigitaxClientConfiguration.Builder]
         */
        fun setDebugModeEnabled(isDebugMode: Boolean) = apply { this.debugMode = isDebugMode }

        /**
         * Setter for setting the base url for the sdk.
         *
         * @param baseUrl REQUIRED: Base Url for fox's api.
         * @return [DigitaxClientConfiguration.Builder]
         */
        fun setBaseUrl(baseUrl: String) = apply { this.baseUrl = baseUrl }

        /**
         * Create the [DigitaxClientConfiguration] object.
         * Will throw [IllegalArgumentException] if required attributes aren't set
         * REQUIRED: [baseUrl]
         * @return [DigitaxClientConfiguration]
         */
        @Throws(IllegalArgumentException::class)
        fun create(): DigitaxClientConfiguration {
            when {
                baseUrl.isNullOrEmpty() -> throw IllegalArgumentException(
                    ILLEGAL_ARGUMENT_EXCEPTION_BASE_URL
                )
                else -> {
                    val commonConfiguration = CommonClientConfigurationProperties()
                    commonConfiguration.baseUrl = baseUrl
                    commonConfiguration.debugMode = debugMode
                    return DigitaxClientConfiguration(
                        commonConfiguration = commonConfiguration
                    )
                }
            }
        }
    }
}