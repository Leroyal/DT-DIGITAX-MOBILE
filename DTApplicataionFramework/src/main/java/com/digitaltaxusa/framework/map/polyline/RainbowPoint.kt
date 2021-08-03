package com.digitaltaxusa.framework.map.polyline

import com.google.android.gms.maps.model.LatLng

/**
 * Represents a point on the rainbow polyline.
 *
 * @property position LatLng The position of the rainbow polygon.
 * @property color Int? The color ID of each point on the rainbow.
 * @constructor
 */
class RainbowPoint(
    val position: LatLng
) {

    /**
     * Retrieve color of the point.
     *
     * @return The color ID of each point on the rainbow.
     */
    var color: Int? = null
        private set

    /**
     * Set color.
     *
     * @param color The color ID of the RainbowPoint.
     * @return [RainbowPoint]
     */
    fun color(color: Int?): RainbowPoint {
        this.color = color
        return this
    }
}