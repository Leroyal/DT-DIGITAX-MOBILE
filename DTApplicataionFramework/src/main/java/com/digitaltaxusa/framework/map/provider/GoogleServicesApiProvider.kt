package com.digitaltaxusa.framework.map.provider

import android.content.Context
import com.digitaltaxusa.framework.constants.Constants.TAG
import com.digitaltaxusa.framework.logger.Logger
import com.digitaltaxusa.framework.map.client.GoogleServicesClient
import com.digitaltaxusa.framework.map.listeners.GoogleServicesApiInterface
import com.digitaltaxusa.framework.map.provider.GoogleServicesApiProvider.getInstance
import com.digitaltaxusa.framework.map.provider.GoogleServicesApiProvider.initialize

const val DESTROY_METHOD_JVM_NAME = "destroy"
const val ERROR_INSTANCE_ALREADY_INITIALIZED =
    "Start using the getInstance() method since GoogleServicesApiProvider has already been initialized."
const val ERROR_INSTANCE_NOT_INITIALIZED =
    "Initialize the GoogleServicesApiProvider first."

/**
 * A singleton instance provider for [GoogleServicesClient]. The client should [initialize] the
 * provider first before using [getInstance].
 *
 * @see [GoogleServicesClient]
 */
object GoogleServicesApiProvider {

    @Volatile
    private var INSTANCE: GoogleServicesApiInterface? = null

    @Throws(IllegalStateException::class)
    fun initialize(
        context: Context
    ) {
        if (INSTANCE == null) {
            synchronized(this) {
                // Assign the instance to local variable to check if it was initialized by
                // some other thread while current thread was blocked to enter the locked zone.
                // If it was initialized then we can return.
                val localInstance = INSTANCE
                if (localInstance == null) {
                    INSTANCE = GoogleServicesClient(
                        context.applicationContext
                    )
                }
            }
        } else {
            Logger.e(TAG, ERROR_INSTANCE_ALREADY_INITIALIZED)
            throw IllegalStateException(ERROR_INSTANCE_ALREADY_INITIALIZED)
        }
    }

    /**
     * Return a singleton instance of [GoogleServicesClient].
     */
    @JvmStatic
    fun getInstance(): GoogleServicesApiInterface {
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