package com.digitaltaxusa.digitax.fragments.map.listeners.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.digitaltaxusa.digitax.fragments.map.TouchableWrapper
import com.digitaltaxusa.digitax.fragments.map.listeners.OnMapTouchListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.UiSettings
import kotlin.math.sqrt

/**
 * Interface to track the touch listener while moving the map.
 *
 * @property context Context Interface to global information about an application environment.
 * @property googleMap GoogleMap This is the main class of the Google Maps SDK for Android
 * and is the entry point for all methods related to the map.
 * @property onMapTouchListener OnMapTouchListener? Listener that indicates when user
 * interacts with the map.
 * @property gestureDetector GestureDetector? Detects various gestures and events using the
 * supplied MotionEvents.
 * @property scaleGestureDetector ScaleGestureDetector? Detects scaling transformation gestures
 * using the supplied MotionEvents.
 * @constructor
 */
class MapTouchListener(
    private val context: Context,
    private val googleMap: GoogleMap? = null,
    private val onMapTouchListener: OnMapTouchListener? = null,
    private val gestureDetector: GestureDetector? = GestureDetector(
        context,
        GestureListener(googleMap), null, true
    ),
    private val scaleGestureDetector: ScaleGestureDetector? = ScaleGestureDetector(
        context,
        ScaleGestureListener(googleMap)
    )
) : TouchableWrapper.MapOnTouchListener {

    // action down touch coordinate (x,y) trackers
    private var touchDownX: Float = 0f
    private var touchDownY: Float = 0f

    override fun onTouch(view: View?, event: MotionEvent) {
        val upX: Float
        val upY: Float

        // set gesture listeners
        gestureDetector?.onTouchEvent(event)
        scaleGestureDetector?.onTouchEvent(event)

        // user interface settings for the map
        val mapSettings: UiSettings? = googleMap?.uiSettings

        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                touchDownX = event.x
                touchDownY = event.y
                // enable scroll gesture so map view can change when not zooming in/out
                mapSettings?.isScrollGesturesEnabled = true
            }
            MotionEvent.ACTION_MOVE -> {
                upX = event.x
                upY = event.y
                val distance = sqrt(
                    ((touchDownX - upX) * (touchDownX - upX) +
                            (touchDownY - upY) * (touchDownY - upY)).toDouble()
                )
                val distanceDP: Float = convertPixelsToDp(
                    context,
                    distance.toFloat()
                )
                if (distanceDP >= MINIMUM_MOVE_THRESHOLD) {
                    // set listener
                    onMapTouchListener?.onMapTouch()
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                // disable scroll and zoom gestures.
                mapSettings?.isScrollGesturesEnabled = false
                mapSettings?.isZoomControlsEnabled = false
                mapSettings?.isZoomGesturesEnabled = false
            }
            MotionEvent.ACTION_POINTER_UP -> {
                // enable scroll gesture so map view can change when not zooming in/out
                mapSettings?.isScrollGesturesEnabled = true
                mapSettings?.isZoomGesturesEnabled = true
            }
        }
    }

    /**
     * Method is used to convert dp to px.
     *
     * @param context Interface to global information about an application environment.
     * @param pixels The pixel value to convert to dp.
     * @return The logical density of the display. This is a scaling factor for the
     * Density Independent Pixel unit, where one DIP is one pixel on an approximately
     * 160 dpi screen (for example a 240x320, 1.5"x2" screen), providing the baseline of
     * the system's display.
     */
    private fun convertPixelsToDp(
        context: Context,
        pixels: Float
    ): Float {
        return pixels / context.resources.displayMetrics.density
    }

    companion object {
        private const val MINIMUM_MOVE_THRESHOLD = 13
    }
}