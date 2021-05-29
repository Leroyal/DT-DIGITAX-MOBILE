package com.digitaltaxusa.framework.http.configuration

/**
 * Common configuration properties. This class prevents redundancy of common configuration
 * attributes across all FoxKit SDK modules. A configuration collects information necessary to
 * perform request operations and instantiate the FoxKit client.
 */
class CommonClientConfigurationProperties {

    /**
     * Debuggable state is optional, is primarily used for mocking behaviors. If in debug mode,
     * http requests are not made. Instead, static stubs are returned back as responses.
     *
     * Flag is set to false by default.
     *
     * @property debugMode OPTIONAL: Toggle for setting debuggable state.
     */
    var debugMode: Boolean? = false

    /**
     * Base url is required for performing requests.
     *
     * <p>The base url is sanitized, so if a base url is set that does not include a protocol,
     * a protocol (https) will automatically be added. If a protocol already exists, no modifications
     * will occur. If you do not include the ending backslash, a backslash will be appended
     * to the end, otherwise no modifications will occur.</p>
     *
     * @property baseUrl REQUIRED: Base url will be used to perform requests.
     */
    var baseUrl: String? = null
        set(value) {
            field = value.let {
                if (!it?.endsWith("/")!!) {
                    "$it/"
                } else {
                    it
                }
            }.let {
                if (it.startsWith("http://") || it.startsWith("https://")) {
                    it
                } else {
                    "https://$it"
                }
            }
        }
}