package com.digitaltaxusa.digitax.fragments.map.listeners

import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.UiSettings

/**
 * Embedded class for detecting zoom and scale gestures
 *
 * <p>Only allows user to zoom once the camera and map stop moving.</p>
 */
class ScaleGestureListener(
    private val googleMap: GoogleMap? = null
) : SimpleOnScaleGestureListener() {

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor

        // user interface settings for the map
        val mapSettings: UiSettings? = googleMap?.uiSettings
        mapSettings?.isScrollGesturesEnabled = false
        mapSettings?.isZoomGesturesEnabled = false

        // represses zoom levels so that the zooming is less sensitive
        // Note: The multipliers are arbitrary values
        val adjustScaleFactor: Float = (scaleFactor - 1.0f) * 0.25f + 1.0f
        val zoom: Float = googleMap?.cameraPosition?.zoom ?: 0f
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                googleMap.cameraPosition.target,
                adjustScaleFactor * zoom
            )
        )
        // set recenter button visibility
//        FrameworkUtils.setViewVisible(ivRecenterMap)
        return true
    }
}