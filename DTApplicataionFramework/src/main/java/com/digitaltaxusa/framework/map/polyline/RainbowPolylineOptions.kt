package com.digitaltaxusa.framework.map.polyline

import android.graphics.*
import java.util.*

/**
 * Rainbow polyline options. Configure properties of the polyline.
 *
 * @property alPoints MutableList<RainbowPoint> The list of [RainbowPoint] to be drawn.
 * @property zIndex Int Positioning of the overlay.
 * @property strokeWidth Int The stroke width.
 * @property strokeCap Cap Cap The stroke cap.
 * @property strokeJoin Join Join The stroke join.
 * @property pathEffect PathEffect? PathEffect The path effect.
 * @property maskFilter MaskFilter? MaskFilter The mask filter.
 * @property strokeShader Shader? Shader The stroke shader.
 * @property strokeColor Int Line segment color in ARGB format.
 * @property isLinearGradient Boolean True if linear gradient should be enabled, otherwise false.
 * @property isAntiAlias Boolean True if antialias should be enabled, otherwise false.
 * @constructor
 */
class RainbowPolylineOptions(
    var alPoints: MutableList<RainbowPoint>? = ArrayList()
) {

    private var zIndex = 0
    private var strokeWidth = 16 //default
    private var strokeCap = Paint.Cap.BUTT // default

    // @note: BEVEL means the outer edges of a join meet with a straight line
    private var strokeJoin = Paint.Join.BEVEL // default
    private var strokeColor = Color.BLACK // default
    private var isLinearGradient = true // default
    private var isAntiAlias = true // default
    private lateinit var pathEffect: PathEffect
    private lateinit var maskFilter: MaskFilter
    private lateinit var strokeShader: Shader

    /**
     * Add a [RainbowPoint] that is used to create a polyline.
     *
     * @param point The [RainbowPoint] to be added.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun add(
        point: RainbowPoint?
    ): RainbowPolylineOptions {
        if (point != null) {
            alPoints?.add(point)
        }
        // return this instance
        return this
    }

    /**
     * Add list of points.
     *
     * @param alPoints The list of [RainbowPoint] to be added.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun add(
        alPoints: List<RainbowPoint>?
    ): RainbowPolylineOptions {
        if (alPoints != null) {
            for (newPoint in alPoints) {
                add(newPoint)
            }
        }
        // return this instance
        return this
    }

    /**
     * Set zIndex.
     *
     * @param zIndex The Z index.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun zIndex(
        zIndex: Int
    ): RainbowPolylineOptions {
        this.zIndex = zIndex
        // return this instance
        return this
    }

    /**
     * Set stroke width.
     *
     * @param strokeWidth The stroke width.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun strokeWidth(
        strokeWidth: Int
    ): RainbowPolylineOptions {
        this.strokeWidth = strokeWidth
        // return this instance
        return this
    }

    /**
     * Set stroke cap.
     *
     * @param strokeCap The stroke cap.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun strokeCap(
        strokeCap: Paint.Cap
    ): RainbowPolylineOptions {
        this.strokeCap = strokeCap
        // return this instance
        return this
    }

    /**
     * Set stroke join.
     *
     * @param strokeJoin The stroke join.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun strokeJoin(
        strokeJoin: Paint.Join
    ): RainbowPolylineOptions {
        this.strokeJoin = strokeJoin
        // return this instance
        return this
    }

    /**
     * Set path effect.
     *
     * @param pathEffect The [PathEffect].
     * @return The [RainbowPolylineOptions] instance.
     */
    fun pathEffect(
        pathEffect: PathEffect
    ): RainbowPolylineOptions {
        this.pathEffect = pathEffect
        // return this instance
        return this
    }

    /**
     * Set mask filter.
     *
     * @param maskFilter The [MaskFilter].
     * @return The [RainbowPolylineOptions] instance.
     */
    fun maskFilter(
        maskFilter: MaskFilter
    ): RainbowPolylineOptions {
        this.maskFilter = maskFilter
        // return this instance
        return this
    }

    /**
     * Set stroke shader.
     *
     * @param strokeShader The [Shader].
     * @return The [RainbowPolylineOptions] instance.
     */
    fun strokeShader(
        strokeShader: Shader
    ): RainbowPolylineOptions {
        this.strokeShader = strokeShader
        // return this instance
        return this
    }

    /**
     * Set linear gradient.
     *
     * @param isLinearGradient True if linear gradient should be enabled, otherwise false.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun linearGradient(
        isLinearGradient: Boolean
    ): RainbowPolylineOptions {
        this.isLinearGradient = isLinearGradient
        // return this instance
        return this
    }

    /**
     * Set stroke color.
     *
     * @param strokeColor Line segment color in ARGB format.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun strokeColor(
        strokeColor: Int
    ): RainbowPolylineOptions {
        this.strokeColor = strokeColor
        // return this instance
        return this
    }

    /**
     * Set antiAlias.
     *
     * @param isAntiAlias True if antiAlias should be enabled, otherwise false.
     * @return The [RainbowPolylineOptions] instance.
     */
    fun antiAlias(
        isAntiAlias: Boolean
    ): RainbowPolylineOptions {
        this.isAntiAlias = isAntiAlias
        // return this instance
        return this
    }

    /**
     * RainbowPolylineOptions builder.
     *
     * @return The new instance of [RainbowPolyline].
     */
    fun build(): RainbowPolyline {
        return RainbowPolyline(
            zIndex,
            alPoints.orEmpty(),
            strokeWidth,
            strokeCap,
            strokeJoin,
            pathEffect,
            maskFilter,
            strokeShader,
            isLinearGradient,
            strokeColor,
            isAntiAlias
        )
    }

    init {
        add(alPoints.orEmpty())
    }
}