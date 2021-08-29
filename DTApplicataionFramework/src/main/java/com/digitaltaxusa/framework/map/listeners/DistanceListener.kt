package com.digitaltaxusa.framework.map.listeners

import org.json.JSONObject

interface DistanceListener {

    /**
     * Interface for when distance between multiple points have been determined.
     *
     * @param response A modifiable set of name/value mappings.
     * @param eta The amount of time it takes to go from one point to another.
     */
    fun onDistanceResponse(
        response: JSONObject?,
        eta: Int?
    )

    /**
     * Interface for when request to retrieve ETA fails.
     */
    fun onDistanceError()
}