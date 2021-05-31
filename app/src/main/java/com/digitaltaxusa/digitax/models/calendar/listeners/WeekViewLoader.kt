package com.digitaltaxusa.digitax.models.calendar.listeners

import com.digitaltaxusa.digitax.models.calendar.weekview.WeekViewEvent
import java.util.*

/**
 * Interface to load weekly views to be displayed on the calendar.
 */
interface WeekViewLoader {

    /**
     * Convert date into a double that will be referenced when loading data.
     *
     * All periods that have the same integer part, define one period. Dates that are
     * later in time should have a greater returned value.
     *
     * @param instance Calendar The Calendar class is an abstract class that provides methods
     * for converting between a specific instant in time and a set of calendar fields
     * such as YEAR, MONTH, DAY_OF_MONTH, HOUR, and so on, and for manipulating the
     * calendar fields, such as getting the date of the next week.
     * @return Double The period index in which the date falls (floating point number).
     */
    fun toWeekViewPeriodIndex(instance: Calendar): Double

    /**
     * Load events within a time period.
     *
     * @param periodIndex Int The time period to load.
     * @return List<WeekViewEvent> List with the events of this period.
     */
    fun onLoad(periodIndex: Int): List<WeekViewEvent>
}