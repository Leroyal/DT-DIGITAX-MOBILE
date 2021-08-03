package com.digitaltaxusa.framework.map.listeners

import com.google.android.gms.maps.model.LatLng

interface DirectionsListener {

    /**
     * Interface for when Google Directions API has successfully provided direction points.
     *
     * @param points List of data points representing a geographic location.
     */
    fun onSuccess(points: List<LatLng>?)

    /**
     * Interface for when Google Directions API fails.
     */
    fun onFailure()
}