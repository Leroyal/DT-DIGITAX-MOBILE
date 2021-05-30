package com.digitaltaxusa.digitax.models.calendar

import java.time.LocalDate

/**
 * Model to represent events.
 *
 * @property eventName String The event name.
 * @property localDate LocalDate The local date.
 * @property type Int The type of event.
 * @constructor
 */
class Event(
    var eventName: String,
    var localDate: LocalDate,
    val type: Int // TODO should update to enumeration??
) : Comparable<Event> {

    override operator fun compareTo(other: Event): Int {
        return localDate.compareTo(other.localDate)
    }
}