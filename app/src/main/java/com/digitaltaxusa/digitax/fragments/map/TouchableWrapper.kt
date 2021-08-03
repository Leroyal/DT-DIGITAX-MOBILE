package com.digitaltaxusa.digitax.fragments.map

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

/**
 * Wrapper to pass the touch screen motion event down to the target view, or this view
 * if it is the target.
 *
 * @param context Interface to global information about an application environment.
 * @property mapOnTouchListener MapOnTouchListener? Interface to track the touch listener
 * while moving the map.
 * @constructor
 */
class TouchableWrapper(
    context: Context
) : FrameLayout(context) {
    private var mapOnTouchListener: MapOnTouchListener? = null

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        // set onTouchListener
        mapOnTouchListener?.onTouch(this, event)
        return super.dispatchTouchEvent(event)
    }

    /**
     * Method is used to set the OnTouchListener.
     *
     * @param listener Callback to check for map touch interaction.
     */
    fun setMapOnTouchListener(listener: MapOnTouchListener?) {
        mapOnTouchListener = listener
    }

    /**
     * Interface to track the touch listener while moving the map.
     */
    interface MapOnTouchListener {
        fun onTouch(view: View?, event: MotionEvent)
    }
}