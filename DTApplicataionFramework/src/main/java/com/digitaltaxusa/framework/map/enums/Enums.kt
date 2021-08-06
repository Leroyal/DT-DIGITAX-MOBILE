package com.digitaltaxusa.framework.map.enums

enum class TravelMode(private val travelMode: String) {
    DRIVING("driving"),
    WALKING("walking");

    override fun toString(): String {
        return travelMode
    }
}