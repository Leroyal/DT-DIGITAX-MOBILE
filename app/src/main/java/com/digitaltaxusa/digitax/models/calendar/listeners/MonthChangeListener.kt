package com.digitaltaxusa.digitax.models.calendar.listeners

import com.digitaltaxusa.digitax.models.calendar.weekview.WeekViewEvent

/**
 * Interface to load events on the calendar and monitor changes on a month
 * by month basis.
 */
interface MonthChangeListener {

    /**
     * The base to load events in the calendar.
     *
     * <p>This method is called three times; once to load the previous month,
     * once to load the next month and once to load the current month.</p>
     *
     * @param newYear Int Year of the events required by the view.
     * @param newMonth Int Month of the events required by the view represented as
     * a numeric value e.g. 1 = January, 2 = February, 3 = March.
     * @return List<WeekViewEvent> List of the events happening during the specified month.
     */
    fun onMonthChange(newYear: Int, newMonth: Int): List<WeekViewEvent>
}