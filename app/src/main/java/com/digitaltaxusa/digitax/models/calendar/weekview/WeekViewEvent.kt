package com.digitaltaxusa.digitax.models.calendar.weekview

import com.digitaltaxusa.digitax.models.calendar.utils.WeekViewUtil
import java.util.*

/**
 * Week by week model view. Contains event information for each week.
 *
 * @property id Long The id of the event.
 * @property color Int The color id of the UI background.
 * @property isAllDay Boolean True if the event is all day, otherwise false.
 * @property name String? Name of the event.
 * @property location String? The location of the event.
 * @property startTime Calendar The time when the event starts.
 * @property endTime Calendar The time when the event ends.
 */
class WeekViewEvent {
    var id: Long = 0
    var color = 0
    var isAllDay = false
    var name: String? = null
    var location: String? = null
    var startTime: Calendar = Calendar.getInstance()
    var endTime: Calendar = Calendar.getInstance()

    /**
     * Initializes the event for week view.
     *
     * @param id Long The id of the event.
     * @param name String? Name of the event.
     * @param startYear Int Year when the event starts.
     * @param startMonth Int Month when the event starts.
     * @param startDay Int Day when the event starts.
     * @param startHour Int Hour (in 24-hour format) when the event starts.
     * @param startMinute Int Minute when the event starts.
     * @param endYear Int Year when the event ends.
     * @param endMonth Int Month when the event ends.
     * @param endDay Int Day when the event ends.
     * @param endHour Int Hour (in 24-hour format) when the event ends.
     * @param endMinute Int Minute when the event ends.
     * @constructor
     */
    constructor(
        id: Long, 
        name: String?, 
        startYear: Int, 
        startMonth: Int, 
        startDay: Int, 
        startHour: Int, 
        startMinute: Int, 
        endYear: Int, 
        endMonth: Int, 
        endDay: Int, 
        endHour: Int, 
        endMinute: Int
    ) {
        this.id = id
        startTime = Calendar.getInstance()
        startTime.set(Calendar.YEAR, startYear)
        startTime.set(Calendar.MONTH, startMonth - 1)
        startTime.set(Calendar.DAY_OF_MONTH, startDay)
        startTime.set(Calendar.HOUR_OF_DAY, startHour)
        startTime.set(Calendar.MINUTE, startMinute)
        endTime = Calendar.getInstance()
        endTime.set(Calendar.YEAR, endYear)
        endTime.set(Calendar.MONTH, endMonth - 1)
        endTime.set(Calendar.DAY_OF_MONTH, endDay)
        endTime.set(Calendar.HOUR_OF_DAY, endHour)
        endTime.set(Calendar.MINUTE, endMinute)
        this.name = name
    }

    /**
     * Initializes the event for week view.
     *
     * @param id Long The id of the event.
     * @param name String? Name of the event.
     * @param location String? The location of the event.
     * @param startTime Calendar The time when the event starts.
     * @param endTime Calendar The time when the event ends.
     * @param allDay Boolean True if the event is all day, otherwise false.
     * @constructor
     */
    constructor(
        id: Long, 
        name: String?, 
        location: String?, 
        startTime: Calendar,
        endTime: Calendar,
        allDay: Boolean
    ) {
        this.id = id
        this.name = name
        this.location = location
        this.startTime = startTime
        this.endTime = endTime
        isAllDay = allDay
    }

    /**
     * Initializes the event for week view.
     *
     * @param id Long Long The id of the event.
     * @param name String? Name of the event.
     * @param startTime Calendar The time when the event starts.
     * @param endTime Calendar The time when the event ends.
     * @constructor
     */
    constructor(
        id: Long,
        name: String?,
        startTime: Calendar,
        endTime: Calendar
    ) : this(id, name, null, startTime, endTime, false)

    /**
     * Method is used to split and categorize [WeekViewEvent] by day.
     *
     * @return List<WeekViewEvent> List of organized [WeekViewEvent].
     */
    fun splitWeekViewEvents(): List<WeekViewEvent> {
        val events: MutableList<WeekViewEvent> = ArrayList()
        // The first millisecond of the next day is still the same day. 
        // No need to split events for this
        var tempEndTime = endTime.clone() as Calendar
        tempEndTime.add(Calendar.MILLISECOND, -1)
        
        // check if dates are the same
        if (!WeekViewUtil.isSameDay(startTime, tempEndTime)) {
            tempEndTime = startTime.clone() as Calendar
            tempEndTime[Calendar.HOUR_OF_DAY] = 23
            tempEndTime[Calendar.MINUTE] = 59
            val weekViewEventA = WeekViewEvent(id, name, location, startTime, tempEndTime, isAllDay)
            weekViewEventA.color = color
            events.add(weekViewEventA)

            // add other days
            val nextDay = startTime.clone() as Calendar
            nextDay.add(Calendar.DATE, 1)
            while (!WeekViewUtil.isSameDay(nextDay, this.endTime)) {
                val overDay = nextDay.clone() as Calendar
                overDay[Calendar.HOUR_OF_DAY] = 0
                overDay[Calendar.MINUTE] = 0
                val endOfOverDay = overDay.clone() as Calendar
                endOfOverDay[Calendar.HOUR_OF_DAY] = 23
                endOfOverDay[Calendar.MINUTE] = 59
                val weekViewEvent = WeekViewEvent(id, name, null, overDay, endOfOverDay, isAllDay)
                weekViewEvent.color = color
                events.add(weekViewEvent)

                // add next day
                nextDay.add(Calendar.DATE, 1)
            }

            // add last day
            val startTime = this.endTime.clone() as Calendar
            startTime[Calendar.HOUR_OF_DAY] = 0
            startTime[Calendar.MINUTE] = 0
            val weekViewEventB = WeekViewEvent(id, name, location, startTime, this.endTime, isAllDay)
            weekViewEventB.color = color
            events.add(weekViewEventB)
        } else {
            events.add(this)
        }
        return events
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val tempWeekViewEvent = other as WeekViewEvent
        return id == tempWeekViewEvent.id
    }

    override fun hashCode(): Int {
        return (id xor (id ushr 32)).toInt()
    }
}