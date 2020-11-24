package com.digitaltaxusa.digitax.api.client.internal.model

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object (DTO) for /api/auth/signin request, where data is encapsulated and
 * transferred over the network.
 *
 * @property deviceType String? String The type of device used for the API call
 * @property email String? String Email required for /api/auth/signin request
 * @property password String? String Password required for /api/auth/signin request
 * @property username String? String Username required for /api/auth/signin request
 * @constructor
 */
internal data class SigninRequestDto(
    @SerializedName("deviceType") val deviceType: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("username") val username: String?
)