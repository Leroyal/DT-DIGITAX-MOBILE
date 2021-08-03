package com.digitaltaxusa.framework.map.listeners

import com.digitaltaxusa.framework.map.model.Place

interface GooglePlacesListener {

    /**
     * Interface for when Google Places API has successfully provided locations.
     *
     * @param places List of location.
     */
    fun onSuccess(places: List<Place>?)

    /**
     * Interface for when Google Places API fails.
     */
    fun onFailure()
}