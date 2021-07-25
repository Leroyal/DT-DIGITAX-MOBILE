package com.digitaltaxusa.digitax.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment

/**
 * Custom map class that override touch events.
 *
 * <p>Creates a map fragment. This constructor is public only for use by an inflater.
 * Use newInstance() to create a SupportMapFragment programmatically.</p>
 */
class MapFragment : SupportMapFragment() {
    private var touchView: TouchableWrapper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val originalContentView: View? = super.onCreateView(inflater, container, savedInstanceState)
        touchView = TouchableWrapper(requireContext())
        // adds a child view. If no layout parameters are already set on the child,
        // the default parameters for this ViewGroup are set on the child
        touchView?.addView(originalContentView)
        return touchView
    }

    override fun getView(): View? {
        return touchView
    }

    /**
     * MapOnTouchListener callback.
     *
     * <p>Interface to track the touch listener while moving the map.</p>
     *
     * @param listener The [TouchableWrapper.MapOnTouchListener] to be set.
     */
    fun setMapTouchListener(listener: TouchableWrapper.MapOnTouchListener?) {
        touchView?.setMapOnTouchListener(listener)
    }
}