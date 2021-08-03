package com.digitaltaxusa.framework.map.listeners

import com.digitaltaxusa.framework.map.model.TurnByTurn
import com.google.android.gms.maps.model.LatLng

interface TurnByTurnListener {

    /**
     * Interface for when Google Directions API has successfully provided turn by turn directions.
     *
     * @param turnByTurnList List<TurnByTurn>? List of turn by turn directions.
     * @param overviewPolyline List<LatLng>? List of [LatLng] that forms a polyline on map.
     */
    fun onSuccess(
        turnByTurnList: List<TurnByTurn>?,
        overviewPolyline: List<LatLng>?
    )

    /**
     * Interface for when Google turn by turn directions fails
     */
    fun onFailure()
}