package com.digitaltaxusa.digitax.api.provider

import android.annotation.SuppressLint
import android.content.Context
import com.digitaltaxusa.digitax.api.client.DigitaxApiClient
import com.digitaltaxusa.digitax.api.client.DigitaxApiInterface
import com.digitaltaxusa.digitax.api.configuration.DigitaxClientConfiguration
import com.digitaltaxusa.digitax.api.provider.DigitaxApiProvider.getInstance
import com.digitaltaxusa.digitax.api.provider.DigitaxApiProvider.initialize
import com.digitaltaxusa.framework.logger.Logger

private const val TAG = "DigitaxApiProvider"
private const val DESTROY_METHOD_JVM_NAME = "destroy"
const val ERROR_INSTANCE_ALREADY_INITIALIZED =
    "Start using the getInstance() method since DigitaxApiClient has already been initialized."
const val ERROR_INSTANCE_NOT_INITIALIZED =
    "Initialize the DigitaxApiClient provider first."
const val ERROR_IGNORING_CONTEXT_CONFIGURATION =
    "Ignoring context and DigitaxClientConfiguration since DigitaxApiClient has already been initialized"

/**
 * A `singleton` instance provider for [DigitaxApiClient]. The client should [initialize] the
 * provider first before using [getInstance].
 *
 * @see [DigitaxApiClient]
 */
object DigitaxApiProvider {

    @SuppressLint("StaticFieldLeak")
    @Volatile
    private var INSTANCE: DigitaxApiInterface? = null

    /**
     * Initialize [DigitaxApiClient].
     *
     * @param context Application context required to initialize [DigitaxApiClient]
     * @param clientConfiguration Configuration with information necessary to perform
     * request operations.
     */
    @JvmStatic
    fun initialize(
        context: Context,
        clientConfiguration: DigitaxClientConfiguration
    ) {
        if (INSTANCE == null) {
            synchronized(this) {
                // Assign the instance to local variable to check if it was initialized by
                // some other thread while current thread was blocked to enter the locked zone.
                // If it was initialized then we can return.
                val localInstance = INSTANCE
                if (localInstance == null) {
                    INSTANCE = DigitaxApiClient(
                        context.applicationContext,
                        clientConfiguration
                    )
                }
            }
        } else {
            Logger.e(TAG, ERROR_IGNORING_CONTEXT_CONFIGURATION)
            throw IllegalStateException(ERROR_INSTANCE_ALREADY_INITIALIZED)
        }
    }

    /**
     * Return a `singleton` instance of [DigitaxApiClient].
     */
    @JvmStatic
    fun getInstance(): DigitaxApiInterface {
        return INSTANCE ?: throw IllegalStateException(ERROR_INSTANCE_NOT_INITIALIZED)
    }

    /**
     * ONLY FOR TESTING PURPOSES. Reset [INSTANCE] as null to help with unit tests.
     */
    @Synchronized
    @JvmStatic
    @JvmName(DESTROY_METHOD_JVM_NAME)
    internal fun destroy() {
        INSTANCE = null
    }
}