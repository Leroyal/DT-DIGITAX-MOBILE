package com.digitaltaxusa.digitax.models.calendar.weekview

import com.digitaltaxusa.digitax.models.calendar.listeners.MonthChangeListener
import com.digitaltaxusa.digitax.models.calendar.listeners.WeekViewLoader
import java.util.*

/**
 * Loads events on a month by month basis, using the [WeekViewLoader].
 *
 * @property onMonthChangeListener MonthChangeListener Load events on the calendar and
 * monitor changes on a month by month basis.
 * @constructor
 */
class MonthLoader(
    var onMonthChangeListener: MonthChangeListener
) : WeekViewLoader {

    override fun toWeekViewPeriodIndex(instance: Calendar): Double {
        return instance[Calendar.YEAR] * 12 + instance[Calendar.MONTH] +
                (instance[Calendar.DAY_OF_MONTH] - 1) / 30.0 // number of days
    }

    override fun onLoad(periodIndex: Int): List<WeekViewEvent> {
        return onMonthChangeListener.onMonthChange(
            periodIndex / 12, periodIndex % 12 + 1
        )
    }
}