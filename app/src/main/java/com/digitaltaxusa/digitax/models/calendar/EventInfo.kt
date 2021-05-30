package com.digitaltaxusa.digitax.models.calendar

/**
 * Model that contains the event information.
 *
 * @property id Int Unique identifier (id) for this event information.
 * @property startTime Long The start time in milliseconds.
 * @property endTime Long The end time in milliseconds.
 * @property eventColor Int The event color.
 * @property isAllDay Boolean True if the event is all day.
 * @property title String? Title of the event.
 * @property timezone String? The event timezone.
 * @property nextNode EventInfo? Information about the next event.
 * @property eventTitles Array<String> List of event titles.
 */
class EventInfo {
    var id = 0
    var startTime: Long = 0
    var endTime: Long = 0
    var eventColor = 0
    var isAllDay = false
    var title: String? = null
    var timezone: String? = null
    var nextNode: EventInfo? = null
    var eventTitles: Array<String> = arrayOf()
}