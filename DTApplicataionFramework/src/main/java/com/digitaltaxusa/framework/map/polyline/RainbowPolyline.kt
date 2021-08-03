package com.digitaltaxusa.framework.map.polyline

import android.graphics.*
import com.google.android.gms.maps.Projection

/**
 * Rainbow polyline created from sets of [RainbowPoint].
 *
 * @property defaultStrokePaint Paint The Paint class holds the style and color information
 * about how to draw geometries, text and bitmaps.
 * @constructor
 */
open class RainbowPolyline(
    zIndex: Int,
    points: List<RainbowPoint>,
    strokeWidth: Int,
    strokeCap: Paint.Cap,
    strokeJoin: Paint.Join,
    pathEffect: PathEffect,
    maskFilter: MaskFilter,
    strokeShader: Shader,
    isLinearGradient: Boolean,
    strokeColor: Int,
    isAntiAlias: Boolean
) : RainbowPolylineShape(
    zIndex,
    points,
    strokeWidth,
    strokeCap,
    strokeJoin,
    pathEffect,
    maskFilter,
    strokeShader,
    isLinearGradient,
    strokeColor,
    isAntiAlias
) {

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
    public override fun doDraw(
        bitmap: Bitmap?,
        projection: Projection,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ) {
        drawStroke(
            bitmap,
            projection,
            alPoints,
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom
        )
    }

    /**
     * Method is used to draw stroke.
     *
     * @param bitmap The [Bitmap] to be drawn.
     * @param projection The [Projection] to be used.
     * @param points2Draw The list of [RainbowPoint] to be drawn.
     * @param paddingLeft The left padding in pixels.
     * @param paddingTop The top padding in pixels.
     * @param paddingRight The right padding in pixels.
     * @param paddingBottom The bottom padding in pixels.
     */
    fun drawStroke(
        bitmap: Bitmap?,
        projection: Projection,
        points2Draw: List<RainbowPoint>,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ) {
        // create new canvas
        val canvas = Canvas(bitmap as Bitmap)
        val paint = defaultStrokePaint
        // last point tracker
        var lastPoint: RainbowPoint? = null
        for (point in points2Draw) {
            if (point.color != null) {
                point.color(strokeColor)
            }
            if (lastPoint != null) {
                drawSegment(
                    canvas,
                    paint,
                    projection,
                    lastPoint,
                    point,
                    paddingLeft,
                    paddingTop,
                    paddingRight,
                    paddingBottom
                )
            }
            lastPoint = point
        }
    }

    /**
     * Method is used to draw (paint) onto the canvas.
     *
     * @param canvas The [Canvas] to be used.
     * @param paint The [Paint] to be used.
     * @param projection The [Projection] to be used.
     * @param pointFrom The start point of the segment.
     * @param pointTo The end point of the segment.
     * @param paddingLeft The left padding in pixels.
     * @param paddingTop The top padding in pixels.
     * @param paddingRight The right padding in pixels.
     * @param paddingBottom The bottom padding in pixels.
     */
    private fun drawSegment(
        canvas: Canvas,
        paint: Paint,
        projection: Projection,
        pointFrom: RainbowPoint,
        pointTo: RainbowPoint,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ) {
        // projection calculations
        val toScreenPoint = projection.toScreenLocation(pointTo.position)
        val fromScreenPoint = projection.toScreenLocation(pointFrom.position)
        val fromX = (fromScreenPoint.x + paddingRight / 2 - paddingLeft / 2).toFloat()
        val fromY = (fromScreenPoint.y + paddingBottom / 2 - paddingTop / 2).toFloat()
        val toX = (toScreenPoint.x + paddingRight / 2 - paddingLeft / 2).toFloat()
        val toY = (toScreenPoint.y + paddingBottom / 2 - paddingTop / 2).toFloat()

        // create gradiant from array of colors (rainbow)
        if (linearGradient) {
            val colors = intArrayOf(pointFrom.color ?: 0, pointTo.color ?: 0)
            // repeat the shader's image horizontally and vertically
            paint.shader = LinearGradient(
                fromX,
                fromY,
                toX,
                toY,
                colors,
                null,
                Shader.TileMode.CLAMP
            )
        } else {
            paint.color = pointFrom.color ?: 0
        }
        paint.shader = strokeShader
        //draw a line segment with the specified start and stop x,y coordinates,
        // using the specified paint
        canvas.drawLine(fromX, fromY, toX, toY, paint)
    }

    /**
     * Method is used to retrieve Paint object.
     *
     * @return The [Paint] object.
     */
    private val defaultStrokePaint: Paint
        get() {
            // create new paint object
            val paint = Paint()
            // set attributes
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.color = strokeColor
            paint.strokeWidth = strokeWidth.toFloat()
            paint.isAntiAlias = antiAlias
            paint.strokeCap = strokeCap
            paint.strokeJoin = strokeJoin
            paint.pathEffect = pathEffect
            paint.maskFilter = maskFilter
            return paint
        }
}