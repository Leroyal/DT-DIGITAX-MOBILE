package com.digitaltaxusa.digitax.api.response.model

import com.digitaltaxusa.digitax.api.response.SigninResponse
import com.digitaltaxusa.framework.http.response.EmptyStateInfo
import com.google.gson.annotations.SerializedName

/**
 * Model class used by [SigninResponse]
 *
 * <p>Encapsulated data used to send information from one subsystem of an application to another.</p>
 *
 * @property id String? Unique id representation of the user
 * @property phone String? Phone number of the user
 * @property email String? Email of the user
 * @property userTypeId String?
 * @property name String? Full name of the user
 * @property firstName String? First name of the user
 * @property lastName String? Last name of the user
 * @property dateOfBirth String? Date of birth of the user
 * @property sex String? Gender (sex) of the user
 * @property verifiedEmail String? 0 if the email is not verified, otherwise 1
 * @property verifiedPhone String? 0 if the phone number is not verified, otherwise 1
 * @property active String? 0 if the user account is not active, otherwise 1
 * @property archived String? 0 if the user account is archived, otherwise 1
 * @property created String? The date which the account was created
 * @property updated String? The date which the account was last updated
 * @property deleted String? 0 if the user account is not deleted, otherwise 1
 * @constructor
 */
data class User(
    @SerializedName("id") val id: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("userTypeId") val userTypeId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null,
    @SerializedName("sex") val sex: String? = null,
    @SerializedName("verifiedEmail") val verifiedEmail: String? = null,
    @SerializedName("verifiedPhone") val verifiedPhone: String? = null,
    @SerializedName("active") val active: String? = null,
    @SerializedName("archived") val archived: String? = null,
    @SerializedName("created") val created: String? = null,
    @SerializedName("updated") val updated: String? = null,
    @SerializedName("deleted") val deleted: String? = null
) : EmptyStateInfo {

    override fun isEmpty(): Boolean = this == EMPTY

    companion object {

        /**
         * An empty object instance for [User].
         *
         * If the API were to respond back with a successful response but with an empty body,
         * clients will get back an [EMPTY] instance for [User].
         */
        val EMPTY = User()
    }
}