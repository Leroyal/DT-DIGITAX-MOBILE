package com.digitaltaxusa.framework.map.model

import com.digitaltaxusa.framework.http.response.EmptyStateInfo
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Model used for providing turn-by-turn directions and plotting connective polylines
 * from an origin and destination.
 *
 * @property instructions String? Instructions to direct users how to get from point A to point B.
 * @property polyline ArrayList<LatLng>? Polyline between origin and destination.
 * @property currentStepDistance String? The walking distance between origin and destination.
 * @property currentStepDuration String? The time it take sto walk between origin and destination.
 * @property maneuver String? Series of required directional moves to get from point A to point B.
 * @property startLocation LatLng? The origin location.
 * @property endLocation LatLng? The destination location.
 * @constructor
 */
data class TurnByTurn(
    var instructions: String? = null,
    var polyline: ArrayList<LatLng>? = null,
    var currentStepDistance: String? = null,
    var currentStepDuration: String? = null,
    var maneuver: String? = null,
    var startLocation: LatLng? = null,
    var endLocation: LatLng? = null
) : EmptyStateInfo {

    override fun isEmpty(): Boolean = this == EMPTY

    companion object {

        /**
         * An empty object instance for [TurnByTurn].
         *
         * If the API were to respond back with a successful response but with an empty body,
         * clients will get back an [EMPTY] instance for [TurnByTurn].
         */
        val EMPTY = TurnByTurn()
    }
}