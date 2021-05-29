package com.digitaltaxusa.digitax.widget.styles

import android.graphics.Canvas
import com.digitaltaxusa.digitax.widget.ParallaxImageView

/**
 * When the imageView is scrolling horizontally, the image in the imageView will be scaled.
 * The scale ratio is according to the horizontal position of the imageView and range
 * from 1.0f to [finalScaleRatio].
 *
 * When the imageView is at the middle of the screen, the scale ratio is 1.0f. And when
 * it scrolls slightly out of the screen, the scale ratio is [finalScaleRatio].
 */
class HorizontalScaleStyle : ParallaxImageView.ParallaxStyle {
    private var finalScaleRatio = 0.7f

    constructor() {}
    constructor(finalScaleRatio: Float) {
        this.finalScaleRatio = finalScaleRatio
    }

    /**
     * Method is used to set the [finalScaleRatio].
     *
     * @param scale Float Set scale value 0f...1.0f.
     */
    fun setFinalScaleRatio(scale: Float) {
        finalScaleRatio = scale
    }

    override fun transform(
        view: ParallaxImageView?,
        canvas: Canvas?,
        x: Int,
        y: Int
    ) {
        val width = view?.width ?: 0
        val paddingLeft = view?.paddingLeft ?: 0
        val paddingRight = view?.paddingRight ?: 0
        val height = view?.height ?: 0
        val paddingTop = view?.paddingTop ?: 0
        val paddingBottom = view?.paddingBottom ?: 0

        // view width and height
        val viewWidth: Int = width - paddingLeft - paddingRight
        val viewHeight: Int = height - paddingTop - paddingBottom
        // device width (pixels)
        val deviceWidth: Int = view?.resources?.displayMetrics?.widthPixels ?: 0

        // check if valid width size
        if (viewWidth >= deviceWidth) {
            // do nothing if imageView width is larger than device width
            return
        }
        // calculate scale
        val scale: Float
        val pivot = (deviceWidth - viewWidth) / 2
        scale = if (x <= pivot) {
            2 * (1 - finalScaleRatio) * (x + viewWidth) / (deviceWidth + viewWidth) + finalScaleRatio
        } else {
            2 * (1 - finalScaleRatio) * (deviceWidth - x) / (deviceWidth + viewWidth) + finalScaleRatio
        }
        canvas?.scale(scale, scale, (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
    }

    override fun onAttachedToImageView(view: ParallaxImageView?) {}
    override fun onDetachedFromImageView(view: ParallaxImageView?) {}
}