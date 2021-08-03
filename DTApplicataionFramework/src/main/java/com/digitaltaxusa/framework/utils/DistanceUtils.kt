package com.digitaltaxusa.framework.utils

import java.text.DecimalFormat

object DistanceUtils {

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
}