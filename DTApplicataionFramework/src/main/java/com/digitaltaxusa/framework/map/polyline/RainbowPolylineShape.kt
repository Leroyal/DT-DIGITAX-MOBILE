package com.digitaltaxusa.framework.map.polyline

import android.graphics.*
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLngBounds
import java.util.*

/**
 * Used to retrieve the zIndex; the order in which this ground overlay is drawn with
 * respect to other overlays.
 *
 * @property zIndex Int Positioning of the overlay.
 * @property strokeWidth Int The stroke width.
 * @property strokeCap Cap The stroke cap.
 * @property strokeJoin Join The stroke join.
 * @property pathEffect PathEffect The path effect.
 * @property maskFilter MaskFilter The mask filter.
 * @property strokeShader Shader The stroke shader.
 * @property strokeColor Int Line segment color in ARGB format.
 * @property linearGradient Boolean True if linear gradient should be enabled, otherwise false.
 * @property antiAlias Boolean True if antialias should be enabled, otherwise false.
 * @property alPoints MutableList<RainbowPoint> The list of [RainbowPoint] to be drawn.
 * @property bounds LatLngBounds? Lat/Lng bounds. Used for setting parameters.
 * @constructor
 */
abstract class RainbowPolylineShape internal constructor(
    var zIndex: Int,
    points: List<RainbowPoint>,
    strokeWidth: Int,
    strokeCap: Paint.Cap,
    strokeJoin: Paint.Join,
    pathEffect: PathEffect,
    maskFilter: MaskFilter,
    strokeShader: Shader,
    linearGradient: Boolean,
    strokeColor: Int,
    antialias: Boolean
) {
    protected var strokeWidth = 16 // default
    protected var strokeCap = Paint.Cap.BUTT // default

    // @note: BEVEL means the outer edges of a join meet with a straight line
    protected var strokeJoin = Paint.Join.BEVEL // default
    protected var pathEffect: PathEffect
    protected var maskFilter: MaskFilter
    protected var strokeShader: Shader
    protected var strokeColor = Color.BLACK // default
    protected var linearGradient = true // default
    protected var antiAlias = true // default

    // list of points
    protected var alPoints: MutableList<RainbowPoint> = ArrayList()

    /**
     * Method is used to populate polyline list. The points have color associations.
     *
     * @param point The [RainbowPoint] to be added.
     * @return The [RainbowPolylineShape] instance.
     */
    fun add(point: RainbowPoint): RainbowPolylineShape {
        if (point.color == null) {
            point.color(strokeColor) // use default if color not set
        }
        alPoints.add(point)
        return this // return this instance
    }

    /**
     * Method is used to retrieve LatLngBounds.
     *
     * @return The [LatLngBounds] of [RainbowPolylineShape.alPoints].
     */
    val bounds: LatLngBounds?
        get() {
            if (alPoints.size == 0 || alPoints.isEmpty()) {
                return null
            }
            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
            for (point in alPoints) {
                builder.include(point.position)
            }
            return builder.build()
        }

    /**
     * Method is used to draw polyline.
     *
     * @param bitmap The [Bitmap] to be drawn.
     * @param projection The [Projection] to be used.
     * @param paddingLeft The left padding in pixels.
     * @param paddingTop The top padding in pixels.
     * @param paddingRight The right padding in pixels.
     * @param paddingBottom The bottom padding in pixels.
     */
    protected abstract fun doDraw(
        bitmap: Bitmap?,
        projection: Projection,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    )

    /**
     * Method is used to draw polyline.
     *
     * @param bitmap The [Bitmap] to be drawn.
     * @param projection The [Projection] to be used.
     * @param paddingLeft The left padding in pixels.
     * @param paddingTop The top padding in pixels.
     * @param paddingRight The right padding in pixels.
     * @param paddingBottom The bottom padding in pixels.
     */
    fun draw(
        bitmap: Bitmap?,
        projection: Projection,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ) {
        if (boundsIntersects(projection.visibleRegion.latLngBounds)) {
            doDraw(bitmap, projection, paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }

    /**
     * Method is used to check if bounds intercept.
     *
     * @param latLngBounds The [LatLngBounds] to be used for the comparison.
     * @return An immutable class representing a latitude/longitude aligned rectangle.
     */
    fun boundsIntersects(
        latLngBounds: LatLngBounds?
    ): Boolean {
        val bounds: LatLngBounds? = bounds
        return if (bounds == null || latLngBounds == null) {
            false
        } else RainbowPolylineUtils.intersectsRectangle(
            latLngBounds.southwest.longitude,
            latLngBounds.southwest.latitude,
            latLngBounds.northeast.longitude,
            latLngBounds.northeast.latitude,
            bounds.southwest.longitude,
            bounds.southwest.latitude,
            bounds.northeast.longitude,
            bounds.northeast.latitude
        )
    }

    init {
        this.strokeWidth = strokeWidth
        this.strokeCap = strokeCap
        this.strokeJoin = strokeJoin
        this.pathEffect = pathEffect
        this.maskFilter = maskFilter
        this.strokeShader = strokeShader
        this.linearGradient = linearGradient
        this.strokeColor = strokeColor
        antiAlias = antialias

        // populate list of LatLng points
        for (point in points) {
            add(point)
        }
    }
}