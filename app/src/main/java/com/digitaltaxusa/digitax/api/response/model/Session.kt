package com.digitaltaxusa.digitax.api.response.model

import com.digitaltaxusa.digitax.api.response.SigninResponse
import com.digitaltaxusa.framework.http.response.EmptyStateInfo
import com.google.gson.annotations.SerializedName

/**
 * Model class used by [SigninResponse]
 *
 * <p>Encapsulated data used to send information from one subsystem of an application to another.</p>
 *
 * @property accessToken String? Object encapsulating the security identity of the users
 * @property expirationMinutes String? The number of minutes until token expiration
 * @property UTCExpirationTime String? Date (UTC) when the token expires
 * @property PTExpirationTime String? Date (PT) when the token expires
 * @constructor
 */
data class Session(
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("expirationMinutes") val expirationMinutes: String? = null,
    @SerializedName("UTCExpirationTime") val UTCExpirationTime: String? = null,
    @SerializedName("PTExpirationTime") val PTExpirationTime: String? = null
) : EmptyStateInfo {

    override fun isEmpty(): Boolean = this == EMPTY

    companion object {

        /**
         * An empty object instance for [Session].
         *
         * If the API were to respond back with a successful response but with an empty body,
         * clients will get back an [EMPTY] instance for [Session].
         */
        val EMPTY = Session()
    }
}