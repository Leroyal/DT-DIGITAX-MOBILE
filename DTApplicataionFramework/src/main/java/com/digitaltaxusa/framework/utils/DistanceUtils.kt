package com.digitaltaxusa.framework.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

object DistanceUtils {

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
        location: LatLng? = LatLng(0.0,0.0)
    ) : String {
        return LATLNG_FORMAT
            .replace(LATITUDE_PLACEHOLDER, location?.latitude.toString())
            .replace(LONGITUDE_PLACEHOLDER, location?.longitude.toString())
    }
}