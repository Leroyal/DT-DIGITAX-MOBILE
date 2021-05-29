package com.digitaltaxusa.framework.widget.styles

import android.graphics.Canvas
import com.digitaltaxusa.framework.widget.ParallaxImageView

private const val INVALID_ALPHA_VALUE = "the alpha must between 0 and 1."

/**
 * When the imageView is scrolling horizontally, the image in imageView will change its alpha.
 * The alpha is calculated according to the horizontal position of the imageView and a range
 * from 1.0f to [finalAlpha].
 *
 * When the imageView is at the middle of the screen, the alpha is 1.0f. And while it scrolls
 * out of screen, it transitions to the [finalAlpha] value.
 *
 * @property finalAlpha Float The alpha applied to the imageView during scrolling.
 */
class HorizontalAlphaStyle : ParallaxImageView.ParallaxStyle {
    private var finalAlpha = 0.3f

    constructor() {}
    constructor(finalAlpha: Float) {
        require(!(finalAlpha < 0 || finalAlpha > 1.0f)) { INVALID_ALPHA_VALUE }
        this.finalAlpha = finalAlpha
    }

    /**
     * Set alpha value.
     *
     * @param alpha Float Set alpha value 0f...1.0f.
     */
    fun setFinalAlpha(alpha: Float) {
        finalAlpha = alpha
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

        // view width
        val viewWidth: Int = width - paddingLeft - paddingRight
        // device width (pixels)
        val deviceWidth: Int = view?.resources?.displayMetrics?.widthPixels ?: 0

        // check if valid width size
        if (viewWidth >= deviceWidth) {
            // do nothing if imageView width is larger than device width
            return
        }
        // calculate alpha
        val alpha: Float
        val pivot = (deviceWidth - viewWidth) / 2
        alpha = if (x <= pivot) {
            2 * (1 - finalAlpha) * (x + viewWidth) / (deviceWidth + viewWidth) + finalAlpha
        } else {
            2 * (1 - finalAlpha) * (deviceWidth - x) / (deviceWidth + viewWidth) + finalAlpha
        }
        view?.alpha = alpha
    }

    override fun onAttachedToImageView(view: ParallaxImageView?) {}
    override fun onDetachedFromImageView(view: ParallaxImageView?) {}
}