package com.digitaltaxusa.framework.map.listeners

import org.json.JSONObject

interface OnWeatherRetrievedListener {

    /**
     * Interface for when Open Weather API has successfully provided weather information.
     *
     * @param weatherObj A modifiable set of name/value mappings.
     */
    fun onSuccess(weatherObj: JSONObject?)

    /**
     * Interface for when Open Weather API fails.
     */
    fun onFailure()
}