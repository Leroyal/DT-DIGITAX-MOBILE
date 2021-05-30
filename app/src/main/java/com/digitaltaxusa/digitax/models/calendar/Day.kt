package com.digitaltaxusa.digitax.models.calendar

class Day {
    private var month = 0
    private var day = 0
    private var year = 0
    private var isToday = false
    private var events: Array<String> = arrayOf()

    // flags
    private var isSelected = false
    private var isEventList = false
    private var isEnabled = false

    /**
     * Setter to flag if the item is an event list.
     *
     * @param isEventList Boolean True if an event list, otherwise false.
     */
    fun setEventList(isEventList: Boolean) {
        this.isEventList = isEventList
    }

    /**
     * Method is used to retrieve event list flag.
     *
     * @return Boolean True if an event list, otherwise false.
     */
    fun isEventList(): Boolean {
        return isEventList
    }

    /**
     * Setter for adding a list of events.
     *
     * @param events Array<String> List of events.
     */
    fun setEvents(events: Array<String>) {
        this.events = events
    }

    /**
     * Method is used to retrieve list of events.
     *
     * @return Array<String> List of events.
     */
    fun getEvents(): Array<String> {
        return events
    }

    /**
     * Setter to flag if the item is selected.
     *
     * @param isSelected Boolean True if the event is selected, otherwise false.
     */
    fun setSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    /**
     * Method is used to retrieve if item is selected flag.
     *
     * @return Boolean True if the event is selected, otherwise false.
     */
    fun isSelected(): Boolean {
        return isSelected
    }

    /**
     * Setter to flag if the date is today.
     *
     * @param isToday Boolean True if the date is today, otherwise false.
     */
    fun setToday(isToday: Boolean) {
        this.isToday = isToday
    }

    /**
     * Method is used to retrieve if the date is today.
     *
     * @return Boolean True if the date is today, otherwise false.
     */
    fun isToday(): Boolean {
        return isToday
    }

    /**
     * Setter to flag if enabled.
     *
     * @param isEnabled Boolean True if enabled, otherwise false.
     */
    fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }

    /**
     * Method is used to retrieve if enabled.
     *
     * @return Boolean True if enabled, otherwise false.
     */
    fun isEnabled(): Boolean {
        return isEnabled
    }

    /**
     * Setter for setting the month. The month is in numeric representation, e.g.
     * 1 = January, 2 = February, 3 = March, ect.
     *
     * @param month Int Numeric representation of the month.
     */
    fun setMonth(month: Int) {
        this.month = month
    }

    /**
     * Method is used to retrieve the month.
     *
     * @return Int Numeric representation of the month.
     */
    fun getMonth(): Int {
        return month
    }

    /**
     * Setter for setting the day. The day is in numeric representation.
     *
     * @param day Int Numeric representation of the day.
     */
    fun setDay(day: Int) {
        this.day = day
    }

    /**
     * Method is used to retrieve the day.
     *
     * @return Int Numeric representation of the day.
     */
    fun getDay(): Int {
        return day
    }

    /**
     * Setter for setting the year.
     *
     * @param year Int The full year e.g. 2021.
     */
    fun setYear(year: Int) {
        this.year = year
    }

    /**
     * Method is used to retrieve the year.
     *
     * @return Int The full year e.g. 2021.
     */
    fun getYear(): Int {
        return year
    }

    /**
     * Override toString() method to construct the full date in the format day/month/year.
     *
     * @return String Full date in the format day/month/year.
     */
    override fun toString(): String {
        return "$day/$month/$year"
    }
}