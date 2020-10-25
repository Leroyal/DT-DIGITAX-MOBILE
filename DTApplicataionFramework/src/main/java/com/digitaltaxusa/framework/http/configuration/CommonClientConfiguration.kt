package com.digitaltaxusa.framework.http.configuration

class CommonClientConfiguration {

    /**
     * Base url is required for performing requests.
     * <p>The base url is sanitized, so if a base url is set that does not include a protocol,
     * a protocol (https) will automatically be added. If a protocol already exists, no modifications
     * will occur. If you do not include the ending backslash, a backslash will be appended
     * to the end, otherwise no modifications will occur</p>
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