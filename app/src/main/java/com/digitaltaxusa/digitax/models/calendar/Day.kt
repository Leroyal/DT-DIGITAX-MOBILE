package com.digitaltaxusa.digitax.models.calendar

/**
 * Model to represent the date.
 *
 * @property month Int Numeric representation of the month.
 * @property day Int Numeric representation of the day.
 * @property year Int The full year e.g. 2021.
 * @property isToday Boolean True if the date is today, otherwise false.
 * @property isSelected Boolean True if the event is selected, otherwise false.
 * @property isEventList Boolean True if an event list, otherwise false.
 * @property isEnabled Boolean True if enabled, otherwise false.
 * @property events Array<String> List of events.
 */
class Day {
    var month = 0
    var day = 0
    var year = 0
    var isToday = false
    var isSelected = false
    var isEventList = false
    var isEnabled = false
    var events: Array<String> = arrayOf()

    /**
     * Override toString() method to construct the full date in the format day/month/year.
     *
     * @return String Full date in the format day/month/year.
     */
    override fun toString(): String {
        return "$day/$month/$year"
    }
}