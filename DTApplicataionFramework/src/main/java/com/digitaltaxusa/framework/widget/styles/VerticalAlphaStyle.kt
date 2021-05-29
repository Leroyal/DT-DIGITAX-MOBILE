package com.digitaltaxusa.framework.widget.styles

import android.graphics.Canvas
import com.digitaltaxusa.framework.widget.ParallaxImageView

private const val INVALID_ALPHA_VALUE = "the alpha must between 0 and 1."

/**
 * When the imageView is scrolling horizontally, the image in imageView will change its alpha.
 * The alpha is calculated according to the vertical position of the imageView and a range
 * from 1.0f to [finalAlpha].
 *
 * When the imageView is at the middle of the screen, the alpha is 1.0f. And while it scrolls
 * out of screen, it transitions to the [finalAlpha] value.
 *
 * @property finalAlpha Float The alpha applied to the imageView during scrolling.
 */
class VerticalAlphaStyle : ParallaxImageView.ParallaxStyle {
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
        val height = view?.height ?: 0
        val paddingTop = view?.paddingTop ?: 0
        val paddingBottom = view?.paddingBottom ?: 0

        // view height
        val viewHeight: Int = height - paddingTop - paddingBottom
        // device height (pixels)
        val deviceHeight: Int = view?.resources?.displayMetrics?.heightPixels ?: 0

        // check if valid width size
        if (viewHeight >= deviceHeight) {
            // do nothing if imageView height is larger than device's height
            return
        }
        // calculate alpha
        val alpha: Float
        val pivot = (deviceHeight - viewHeight) / 2
        alpha = if (y <= pivot) {
            2 * (1 - finalAlpha) * (y + viewHeight) / (deviceHeight + viewHeight) + finalAlpha
        } else {
            2 * (1 - finalAlpha) * (deviceHeight - y) / (deviceHeight + viewHeight) + finalAlpha
        }
        view?.alpha = alpha
    }

    override fun onAttachedToImageView(view: ParallaxImageView?) {}
    override fun onDetachedFromImageView(view: ParallaxImageView?) {}
}