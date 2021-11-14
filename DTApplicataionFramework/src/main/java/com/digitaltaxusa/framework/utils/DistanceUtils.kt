package com.digitaltaxusa.framework.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

object DistanceUtils {

    private const val DEFAULT_PROXIMITY_DISTANCE = 15f // meters
    private const val LATITUDE_PLACEHOLDER = "{latitude}"
    private const val LONGITUDE_PLACEHOLDER = "{longitude}"
    private const val LATLNG_FORMAT = "$LATITUDE_PLACEHOLDER,$LONGITUDE_PLACEHOLDER"

    /**
     * Method is used to convert meters to miles.
     *
     * @param meters The distance (meters) to convert to miles.
     * @return The converted miles.
     */
    fun meterToMile(meters: Float): Int {
        val miles = meters / 1609.344
        val formatter = DecimalFormat("######")
        return try {
            formatter.format(miles).toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            // return 0
            0
        }
    }

    /**
     * Method is used to convert meters to feet.
     *
     * @param meters The meter value to be converted to feet.
     * @return The converted value in feet.
     */
    fun metersToFeet(meters: Float): Int {
        val feet = meters * 3.281
        val formatter = DecimalFormat("######")
        return try {
            formatter.format(feet).toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            // return 0
            0
        }
    }

    /**
     * Method is used to convert feet to meters.
     *
     * @param feet The feet value to be converted to meters.
     * @return The converted value in meters.
     */
    fun feetToMeters(feet: Float): Double {
        return feet * 0.3048
    }

    /**
     * Method is used to convert [Location] into a String latitude/longitude representation.
     * The String format is "latitude,longitude" e.g. 34.4520644,-118.4977783.
     *
     * @param location Location A data class representing a geographic location.
     * @return String Formatted latitude/longitude representation.
     */
    fun getLatLngAsString(
        location: LatLng? = LatLng(0.0, 0.0)
    ): String {
        return LATLNG_FORMAT
            .replace(LATITUDE_PLACEHOLDER, location?.latitude.toString())
            .replace(LONGITUDE_PLACEHOLDER, location?.longitude.toString())
    }

    /**
     * Will determine if two points are within proximity of each other using a radius.
     * The distance measurements are in meters.
     *
     * @param originLocation Location A data class representing a geographic location.
     * @param currentLocation Location A data class representing a geographic location.
     * @param radiusInMeters Float The radius to see if two locations are in proximity
     * of each other.
     * @return Boolean True of two locations are in proximity of each other based on
     * the provided radius.
     */
    fun isLocationWithinRadius(
        originLocation: Location,
        currentLocation: Location,
        radiusInMeters: Float = DEFAULT_PROXIMITY_DISTANCE
    ): Boolean {
        val distance = FloatArray(1)
        // computes the approximate distance in meters between two locations,
        // and optionally the initial and final bearings of the shortest path
        // between them
        Location.distanceBetween(
            originLocation.latitude,
            originLocation.longitude,
            currentLocation.latitude,
            currentLocation.longitude,
            distance
        )
        // 1KM = 1000 meter
        return distance[0] < (radiusInMeters * 1000)
    }
}