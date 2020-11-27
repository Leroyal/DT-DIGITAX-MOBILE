package com.digitaltaxusa.digitax.api.client.internal.adapter

import com.digitaltaxusa.digitax.api.client.internal.model.SigninRequestDto
import com.digitaltaxusa.digitax.api.client.internal.model.SignupRequestDto
import com.digitaltaxusa.digitax.api.requests.SigninRequest
import com.digitaltaxusa.digitax.api.requests.SignupRequest

/**
 * Allows the interface of an existing class [SigninRequest] to be used as another
 * interface [SigninRequestDto].
 *
 * <p>This pattern is often used to make existing classes work with others without modifying
 * their source code.</p>
 *
 * @param signinRequest [SigninRequest] object for making /api/auth/signin request.
 * @return SigninRequestDto
 */
internal fun adapt(signinRequest: SigninRequest) = SigninRequestDto(
    deviceType = signinRequest.deviceType,
    email = signinRequest.email,
    password = signinRequest.password,
    username = signinRequest.username
)

/**
 * Allows the interface of an existing class [SignupRequest] to be used as another
 * interface [SignupRequestDto].
 *
 * <p>This pattern is often used to make existing classes work with others without modifying
 * their source code.</p>
 *
 * @param signupRequest [SignupRequest] object for making /api/auth/signup request.
 * @return SignupRequestDto
 */
internal fun adapt(signupRequest: SignupRequest) = SignupRequestDto(
    deviceType = signupRequest.deviceType,
    email = signupRequest.email,
    password = signupRequest.password,
    username = signupRequest.username,
    role = signupRequest.role
)