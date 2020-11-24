package com.digitaltaxusa.digitax.api.client.internal.adapter

import com.digitaltaxusa.digitax.api.client.internal.model.SigninRequestDto
import com.digitaltaxusa.digitax.api.requests.SigninRequest

/**
 * Allows the interface of an existing class [SigninRequest] to be used as another
 * interface [SigninRequestDto].
 *
 * <p>This pattern is often used to make existing classes work with others without modifying
 * their source code.</p>
 *
 * @param signinRequest SigninRequest Object for marking /api/auth/signin request.
 * @return SigninRequestDto
 */
internal fun adapt(signinRequest: SigninRequest) = SigninRequestDto(
    deviceType = signinRequest.deviceType,
    email = signinRequest.email,
    password = signinRequest.password,
    username = signinRequest.username
)