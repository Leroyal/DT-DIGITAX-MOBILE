package com.digitaltaxusa.digitax.network.listeners

interface NetworkStatusObserver {

    /**
     * Interface for monitoring network status change.
     */
    fun notifyConnectionChange(isConnected: Boolean)
}