package com.digitaltaxusa.digitax.models.calendar.utils

import java.util.*

object WeekViewUtil {

    /**
     * Method is used to check if two [Calendar] days are the same.
     *
     * @param dayOne Calendar? The origin day.
     * @param dayTwo Calendar? The day to compare.
     * @return Boolean True if the two days are the same, otherwise false.
     */
    fun isSameDay(
        dayOne: Calendar,
        dayTwo: Calendar
    ): Boolean {
        return dayOne[Calendar.YEAR] == dayTwo[Calendar.YEAR] &&
                dayOne[Calendar.DAY_OF_YEAR] == dayTwo[Calendar.DAY_OF_YEAR]
    }

    /**
     * Returns a calendar instance at the start of this day
     *
     * @return Calendar Gets a calendar using the default time zone and locale.
     */
    fun today(): Calendar {
        val today = Calendar.getInstance()
        today[Calendar.HOUR_OF_DAY] = 0
        today[Calendar.MINUTE] = 0
        today[Calendar.SECOND] = 0
        today[Calendar.MILLISECOND] = 0
        return today
    }
}