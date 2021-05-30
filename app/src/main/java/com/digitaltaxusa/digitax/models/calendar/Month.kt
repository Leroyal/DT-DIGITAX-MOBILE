package com.digitaltaxusa.digitax.models.calendar

import java.util.*

/**
 * Model to represent the month.
 *
 * @property month Int Numeric representation of the month.
 * @property year Int The full year e.g. 2021.
 * @property numberOfDay Int Numeric representation of the day.
 * @property numberOfWeek Int Numeric representation of the week.
 * @property firstDay Int The first day.
 * @property monthName String? The month name.
 * @property days ArrayList<Day> List of days for the month.
 */
class Month {
    var month = 0
    var year = 0
    var numberOfDay = 0
    var numberOfWeek = 0
    var firstDay = 0
    var monthName: String? = null
    var days: ArrayList<Day> = arrayListOf()
}