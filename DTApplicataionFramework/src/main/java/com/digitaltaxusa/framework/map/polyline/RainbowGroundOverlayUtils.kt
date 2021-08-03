package com.digitaltaxusa.framework.map.polyline

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.maps.android.SphericalUtil
import java.util.*

/**
 * A ground overlay is an image that is fixed to a map. A ground overlay has the
 * following properties: shape, padding, spherical roundness of arc, and more.
 *
 * @see [GroundOverlay](https://developers.google.com/android/reference/com/google/android/gms/maps/model/GroundOverlay)
 */
class RainbowGroundOverlayUtils private constructor(
    var view: View,
    var map: GoogleMap,
    private var zIndex: Float,
    private var paddingLeft: Int,
    private var paddingTop: Int,
    private var paddingRight: Int,
    private var paddingBottom: Int
) {
    private var overlay: GroundOverlay? = null
    private var bitmap: Bitmap? = null
    private var isPolylineRefreshed = false

    // sorted map of rainbow shapes
    private val shapes: SortedMap<Int, MutableList<RainbowPolylineShape>> = TreeMap()

    /**
     * Method is used to refresh
     */
    fun refresh() {
        if (isPolylineRefreshed) {
            return
        }
        // set flag only if polyline exists
        isPolylineRefreshed = shapes.size > 0
        val cameraPosition = map.cameraPosition
        if (cameraPosition.zoom >= MINIMUM_ZOOM_LEVEL) {
            val projection = map.projection

            // prepare empty new bitmap
            prepareBitmap()
            // draw on top of bitmap
            draw(bitmap, projection)
            val mapWidth = SphericalUtil.computeDistanceBetween(
                projection.visibleRegion.nearLeft,
                projection.visibleRegion.nearRight
            ).toFloat()

            if (overlay != null) {
                val background = GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromBitmap(bitmap as Bitmap))
                    .position(cameraPosition.target, mapWidth)
                    .bearing(cameraPosition.bearing)
                    .zIndex(zIndex)
                overlay = map.addGroundOverlay(background)
            } else {
                overlay?.setImage(BitmapDescriptorFactory.fromBitmap(bitmap as Bitmap))
                overlay?.position = cameraPosition.target
                overlay?.setDimensions(mapWidth)
                overlay?.bearing = cameraPosition.bearing
            }
        } else {
            if (overlay != null) {
                // release resources
                System.gc()
                overlay?.remove()
                overlay = null
            }
        }
    }

    /**
     * Method is used to add polyline shape.
     *
     * @param shape The [RainbowPolylineShape] to be used to dictate the appearance of the polyline.
     */
    fun addPolylineShape(
        shape: RainbowPolylineShape?
    ) {
        if (shape != null) {
            if (!shapes.containsKey(shape.zIndex)) {
                shapes[shape.zIndex] = ArrayList()
            }
            val shapesZIndex = shapes[shape.zIndex]
            shapesZIndex?.add(shape)
        }
    }

    /**
     * Method is used to remove polyline shape.
     *
     * @param shape The [RainbowPolylineShape] to remove.
     */
    fun removePolylineShape(
        shape: RainbowPolylineShape?
    ) {
        if (shape != null) {
            val zIndices: Set<Int> = shapes.keys
            for (zIndex in zIndices) {
                val shapesZIndex = shapes[zIndex]
                shapesZIndex?.remove(shape)
                // set flag
                isPolylineRefreshed = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // remove shape
                    shapes.remove(shape.zIndex, ArrayList())
                } else {
                    // remove shape
                    val itr: MutableIterator<Map.Entry<Int, List<RainbowPolylineShape>>> =
                        shapes.entries.iterator()
                    while (itr.hasNext()) {
                        val entry = itr.next()
                        if (entry.key == 0) {
                            itr.remove()
                        }
                    }
                }
            }
        }
    }

    /**
     * Method is used to fetch new bitmap.
     */
    private fun prepareBitmap() {
        if (bitmap == null || bitmap?.width != view.width || bitmap?.height != view.height) {
            // create new bitmap
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        } else {
            // erase bitmap; fill bitmap with transparent color
            bitmap?.eraseColor(Color.TRANSPARENT)
        }
    }

    /**
     * Method is used to draw onto bitmap.
     *
     * @param bitmap The [Bitmap] to be drawn.
     * @param projection The [Projection] to be used.
     * @return The drawn Bitmap
     */
    private fun draw(
        bitmap: Bitmap?,
        projection: Projection
    ): Bitmap? {
        val zIndices: Set<Int> = shapes.keys
        for (zIndex in zIndices) {
            draw(bitmap, projection, shapes[zIndex].orEmpty())
        }
        return bitmap
    }

    /**
     * Method is used to draw onto bitmap.
     *
     * @param bitmap The [Bitmap] to be drawn.
     * @param projection The [Projection] to be used.
     * @param shapes The list of [RainbowPolylineShape] to be used.
     * @return The drawn [Bitmap].
     */
    private fun draw(
        bitmap: Bitmap?,
        projection: Projection,
        shapes: List<RainbowPolylineShape>
    ): Bitmap? {
        for (shape in shapes) {
            shape.draw(bitmap, projection, paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
        return bitmap
    }

    /**
     * Builder for [RainbowGroundOverlayUtils]. Set properties for rainbow polyline.
     *
     * @property view View This class represents the basic building block for user interface
     * components.
     * @property map GoogleMap Map provided by Google.
     * @property zIndex Float Positioning of the overlay.
     * @property paddingLeft Int The left padding in pixels.
     * @property paddingTop Int The top padding in pixels.
     * @property paddingRight Int The right padding in pixels.
     * @property paddingBottom Int The bottom padding in pixels.
     * @constructor
     */
    class Builder(
        private val view: View,
        private val map: GoogleMap
    ) {
        private var zIndex = 0f
        private var paddingLeft = 0
        private var paddingTop = 0
        private var paddingRight = 0
        private var paddingBottom = 0

        /**
         * Method is used to set the Z index.
         *
         * @param zIndex The Z index.
         * @return The [Builder] object.
         */
        fun zIndex(zIndex: Float): Builder {
            this.zIndex = zIndex
            return this
        }

        /**
         * Method is used to set the padding.
         *
         * @param paddingLeft The left padding in pixels.
         * @param paddingTop The top padding in pixels.
         * @param paddingRight The right padding in pixels.
         * @param paddingBottom The bottom padding in pixels.
         * @return The [Builder] object.
         */
        fun padding(
            paddingLeft: Int,
            paddingTop: Int,
            paddingRight: Int,
            paddingBottom: Int
        ): Builder {
            this.paddingTop = paddingTop
            this.paddingBottom = paddingBottom
            this.paddingLeft = paddingLeft
            this.paddingRight = paddingRight
            return this
        }

        /**
         * Method is used to initialize the [RainbowGroundOverlayUtils].
         *
         * @return The [RainbowGroundOverlayUtils] object.
         */
        fun build(): RainbowGroundOverlayUtils {
            return RainbowGroundOverlayUtils(
                view,
                map,
                zIndex,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom
            )
        }
    }

    companion object {
        // represents the minimum zoom level allowed before polylines stop being
        // redrawn (stop refreshing)
        private const val MINIMUM_ZOOM_LEVEL = 8f // arbitrary value. Added for optimization reasons
    }

    init {
        // currently tilt gestures is not handled. Disabling for now --LT
        map.uiSettings.isTiltGesturesEnabled = false
    }
}