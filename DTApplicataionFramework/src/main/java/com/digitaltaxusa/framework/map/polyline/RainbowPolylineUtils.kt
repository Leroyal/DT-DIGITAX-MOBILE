package com.digitaltaxusa.framework.map.polyline

/**
 * Rainbow polyline utils.
 *
 * @constructor Create empty Rainbow polyline utils.
 */
object RainbowPolylineUtils {

    /**
     * Utility method is used to check if bounds intercept.
     *
     * @param checkMinX The first min X value.
     * @param checkMinY The first min Y value.
     * @param checkMaxX The first max X value.
     * @param checkMaxY The first max Y value.
     * @param againstMinX The second min X value.
     * @param againstMinY The second min Y value.
     * @param againstMaxX The second max X value.
     * @param againstMaxY The second max Y value.
     * @return True is intersects boundaries, otherwise false.
     */
    @JvmStatic
    fun intersectsRectangle(
        checkMinX: Double,
        checkMinY: Double,
        checkMaxX: Double,
        checkMaxY: Double,
        againstMinX: Double,
        againstMinY: Double,
        againstMaxX: Double,
        againstMaxY: Double
    ): Boolean {
        var output = false
        if (againstMaxX > checkMinX && againstMinX < checkMaxX &&
            againstMaxY > checkMinY && againstMinY < checkMaxY
        ) {
            output = true
        }
        return output
    }
}