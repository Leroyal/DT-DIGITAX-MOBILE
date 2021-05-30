package com.digitaltaxusa.digitax.models.calendar

import java.time.LocalDate
import java.util.*

/**
 * Model for adding events.
 *
 * @property events ArrayList<Event> List of events.
 * @property indexTracker HashMap<LocalDate, Int> Map to track local date by index.
 * @property months ArrayList<Month> List of months.
 * @constructor
 */
class AddEvent(
    var events: ArrayList<Event>,
    var indexTracker: HashMap<LocalDate, Int>,
    var months: ArrayList<Month>
)