package com.digitaltaxusa.digitax.fragments.map.listeners.gestures

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.digitaltaxusa.digitax.fragments.map.listeners.OnRecenterMapListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.UiSettings

private const val MAP_SCALE_FACTOR = 1.0f

/**
 * Embedded class for gestures.
 *
 * <p>Used to handle gestures such as double-tapping the map.</p>
 *
 * @property googleMap GoogleMap? This is the main class of the Google Maps SDK for
 * Android and is the entry point for all methods related to the map.
 * @property onRecenterMapListener OnRecenterMapListener? Listener that indicates when to
 * show the map recenter button based on user interaction with the map.
 * @property scaleFactor Float Adjusts how much more additional zoom happens after
 * double tapping the map.
 * @constructor
 */
class GestureListener(
    private val googleMap: GoogleMap? = null,
    private val onRecenterMapListener: OnRecenterMapListener? = null
) : SimpleOnGestureListener() {

    private val scaleFactor: Float = MAP_SCALE_FACTOR // set default

    override fun onDoubleTap(event: MotionEvent): Boolean {
        // user interface settings for the map
        val mapSettings: UiSettings? = googleMap?.uiSettings
        mapSettings?.isScrollGesturesEnabled = false
        mapSettings?.isZoomControlsEnabled = false
        mapSettings?.isZoomGesturesEnabled = false

        // adjusts how much more additional zoom happens after double tapping
        val adjustScaleFactor: Float = (scaleFactor - 0.8f) * 0.3f + 1.0f
        val zoom: Float = googleMap?.cameraPosition?.zoom ?: 0f
        val center = CameraUpdateFactory.newLatLngZoom(
            googleMap?.cameraPosition?.target,
            adjustScaleFactor * zoom
        )
        // animates the movement of the camera from the current position to the position
        // defined in the update
        googleMap?.animateCamera(center)
        // set recenter button visibility
        onRecenterMapListener?.onRecenterMap(true)
        return false
    }
}