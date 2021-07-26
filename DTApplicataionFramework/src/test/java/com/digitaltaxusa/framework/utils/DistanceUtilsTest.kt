package com.digitaltaxusa.framework.utils

import org.junit.Assert.assertEquals
import org.junit.Test

private const val DELTA = 0.01
private const val DISTANCE_UNIT_01 = 100f
private const val DISTANCE_UNIT_02 = 100.0f
private const val DISTANCE_UNIT_03 = 500f
private const val DISTANCE_UNIT_04 = 1609.35f
private const val DISTANCE_UNIT_05 = 3218.70f

class DistanceUtilsTest {

    @Test
    fun checkMeterToMileCalculations() {
        assertEquals(0, DistanceUtils.meterToMile(DISTANCE_UNIT_01))
        assertEquals(0, DistanceUtils.meterToMile(DISTANCE_UNIT_02))
        assertEquals(0, DistanceUtils.meterToMile(DISTANCE_UNIT_03))
        assertEquals(1, DistanceUtils.meterToMile(DISTANCE_UNIT_04))
        assertEquals(2, DistanceUtils.meterToMile(DISTANCE_UNIT_05))
    }

    @Test
    fun checkMeterToFeetCalculations() {
        assertEquals(328, DistanceUtils.metersToFeet(DISTANCE_UNIT_01))
        assertEquals(328, DistanceUtils.metersToFeet(DISTANCE_UNIT_02))
        assertEquals(1640, DistanceUtils.metersToFeet(DISTANCE_UNIT_03))
        assertEquals(5280, DistanceUtils.metersToFeet(DISTANCE_UNIT_04))
        assertEquals(10561, DistanceUtils.metersToFeet(DISTANCE_UNIT_05))
    }

    @Test
    fun checkFeetToMeterCalculations() {
        assertEquals(30.48, DistanceUtils.feetToMeters(DISTANCE_UNIT_01), DELTA)
        assertEquals(30.48, DistanceUtils.feetToMeters(DISTANCE_UNIT_02), DELTA)
        assertEquals(152.4, DistanceUtils.feetToMeters(DISTANCE_UNIT_03), DELTA)
        assertEquals(490.52987255859375, DistanceUtils.feetToMeters(DISTANCE_UNIT_04), DELTA)
        assertEquals(981.0597451171875, DistanceUtils.feetToMeters(DISTANCE_UNIT_05), DELTA)
    }
}