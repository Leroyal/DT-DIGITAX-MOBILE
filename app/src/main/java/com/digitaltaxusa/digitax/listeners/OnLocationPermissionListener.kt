package com.digitaltaxusa.digitax.listeners

interface OnLocationPermissionListener {

    /**
     * Interface to track when location permissions are enabled.
     *
     * @param isEnabled True if location permissions are enabled, otherwise false.
     */
    fun onLocationPermission(isEnabled: Boolean)
}