package com.digitaltaxusa.digitax.api.response

import com.digitaltaxusa.digitax.api.response.enums.EndpointIdentifier
import com.digitaltaxusa.digitax.api.response.models.Session
import com.digitaltaxusa.digitax.api.response.models.User
import com.digitaltaxusa.framework.http.response.EmptyStateInfo
import com.google.gson.annotations.SerializedName

/**
 * Data transfer object (DTO) for /api/auth/signin, where all data to be used by the view
 * is fetched and funneled into the DTO before returning control to the presentation tier.
 *
 * @property user User? Object that contains user information
 * @property session Session? Object that contains session information
 * @constructor
 */
data class ForgotPasswordResponse(
    @SerializedName("user") val user: User? = null,
    @SerializedName("session") val session: Session? = null,
    private val identifier: EndpointIdentifier = EndpointIdentifier.FORGOT_PASSWORD
) : EmptyStateInfo {

    override fun isEmpty(): Boolean = this == EMPTY

    companion object {

        /**
         * An empty object instance for [ForgotPasswordResponse].
         *
         * If the API were to respond back with a successful response but with an empty body,
         * clients will get back an [EMPTY] instance for [ForgotPasswordResponse].
         */
        val EMPTY = ForgotPasswordResponse()
    }
}