package com.digitaltaxusa.digitax.models.calendar.listeners

import android.graphics.RectF
import com.digitaltaxusa.digitax.models.calendar.weekview.WeekViewEvent

/**
 * Interface used to track when events are clicked on the calendar.
 */
interface EventClickListener {

    /**
     * Listener for when an event is clicked.
     *
     * @param event WeekViewEvent? The selected event.
     * @param eventRect RectF? View containing the selected event.
     */
    fun onEventClick(event: WeekViewEvent?, eventRect: RectF?)
}