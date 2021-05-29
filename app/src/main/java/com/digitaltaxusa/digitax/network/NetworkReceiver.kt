package com.digitaltaxusa.digitax.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.digitaltaxusa.digitax.constants.Constants
import com.digitaltaxusa.digitax.network.listeners.NetworkStatusObserver
import com.digitaltaxusa.framework.network.NetworkUtils
import java.util.*

/**
 * Class used to monitor network connection strength. When the network is having
 * issues a broadcast message is sent and used to display a network dialog message.
 */
class NetworkReceiver : BroadcastReceiver() {
    private val observerList: MutableList<NetworkStatusObserver> =
        ArrayList<NetworkStatusObserver>()
    private var isPreviousNetworkConnected = true

    override fun onReceive(context: Context, intent: Intent) {
        val disconnected = intent.getBooleanExtra(
            ConnectivityManager.EXTRA_NO_CONNECTIVITY,
            false
        )

        // check if connectivity status has changed
        val isCurrentNetworkConnected: Boolean = if (disconnected) {
            false
        } else {
            NetworkUtils.isConnected(context)
        }

        if (isCurrentNetworkConnected != isPreviousNetworkConnected) {
            isPreviousNetworkConnected = isCurrentNetworkConnected
            notifyObservers(isPreviousNetworkConnected)
        }
    }

    /**
     * Informs all [NetworkStatusObserver] if the device is connected to a network.
     *
     * @param isNetworkConnectedCurrent True if device is connected to a network, otherwise false.
     */
    private fun notifyObservers(isNetworkConnectedCurrent: Boolean) {
        for (networkStatusObserver in observerList) {
            networkStatusObserver.notifyConnectionChange(isNetworkConnectedCurrent)
        }
    }

    /**
     * Add observer to observer list.
     *
     * @param observer List of observers that track network activity.
     */
    fun addObserver(observer: NetworkStatusObserver) {
        observerList.add(observer)
    }

    /**
     * Remove observer from observer list.
     *
     * @param observer List of observers that track network activity.
     */
    fun removeObserver(observer: NetworkStatusObserver) {
        observerList.remove(observer)
    }

    /**
     * Retrieve observer list size.
     *
     * @return The observer list size.
     */
    fun getObserverSize(): Int {
        return observerList.size
    }

    /**
     * Check if receiver is added to observer list.
     *
     * @param observer List of observers that track network activity.
     * @return True if receiver is added to observer list.
     */
    operator fun contains(observer: NetworkStatusObserver): Boolean {
        return observerList.contains(observer)
    }

    /**
     * Method is used to print observer list.
     *
     * <p>Primarily used for debugging.</p>
     */
    fun printObserverList() {
        Log.i(Constants.TAG, "===== PRINT OBSERVER LIST ===== ")
        for (i in observerList.indices) {
            Log.i(
                Constants.TAG,
                String.format(
                    Locale.US,
                    "item(%d): %s",
                    i,
                    observerList.get(i).toString()
                )
            )
        }
    }
}