package com.digitaltaxusa.digitax.fragments.map.listeners

interface OnRecenterMapListener {

    /**
     * Interface to toggle visibility of the map recenter button based on user interaction
     * with Google map.
     *
     * @param isVisible True if location permissions are enabled, otherwise false.
     */
    fun onRecenterMap(isVisible: Boolean)
}