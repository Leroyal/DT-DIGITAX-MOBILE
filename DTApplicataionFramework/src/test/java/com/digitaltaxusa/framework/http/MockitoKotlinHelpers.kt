package com.digitaltaxusa.framework.http

import org.mockito.ArgumentCaptor

/**
 * Returns ArgumentCaptor.capture() as a nullable type to avoid java.lang.IllegalStateException when null is returned.
 *
 * This issue is documented here:
 * - https://github.com/mockito/mockito/issues/1255
 */
inline fun <reified T : Any?> capture(argumentCaptor: ArgumentCaptor<T?>): T =
    argumentCaptor.capture() ?: castNull()

/**
 * A workaround for now is to cast null objects as T.
 * Will need to update this method to use the new `@JvmUnchecked` annotation as soon as Kotlin 1.3.40 releases.
 *
 * References:
 * - https://github.com/nhaarman/mockito-kotlin/issues/310
 * - https://youtrack.jetbrains.com/issue/KT-26946
 */
@Suppress("UNCHECKED_CAST")
fun <T> castNull(): T = null as T