package com.digitaltaxusa.digitax.widget.styles

import android.graphics.Canvas
import android.widget.ImageView
import com.digitaltaxusa.digitax.widget.ParallaxImageView
import kotlin.math.abs

/**
 * When the imageView is scrolling vertically, the image in imageView will also
 * scroll horizontally if the image width is larger than the imageView width.
 *
 * Note: The image will not over scroll to it's view bounds.
 *
 * <p>Only supports imageView with CENTER_CROP scale type.</p>
 */
class VerticalMovingStyle : ParallaxImageView.ParallaxStyle {

    override fun onAttachedToImageView(view: ParallaxImageView?) {
        // only supports CENTER_CROP
        view?.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    override fun onDetachedFromImageView(view: ParallaxImageView?) {}

    override fun transform(
        view: ParallaxImageView?,
        canvas: Canvas?,
        x: Int,
        y: Int
    ) {
        var tempY = y
        // only supports CENTER_CROP. If any other scale type return
        if (view?.scaleType !== ImageView.ScaleType.CENTER_CROP) {
            return
        }

        // image width and height
        val imageWidth: Int = view.drawable.intrinsicWidth
        val imageHeight: Int = view.drawable.intrinsicHeight
        if (imageWidth <= 0 || imageHeight <= 0) {
            return
        }

        // view width and height
        val viewWidth: Int = view.width - view.paddingLeft - view.paddingRight
        val viewHeight: Int = view.height - view.paddingTop - view.paddingBottom

        // device height
        val deviceHeight: Int = view.resources.displayMetrics.heightPixels
        if (imageWidth * viewHeight < imageHeight * viewWidth) {
            // avoid over scroll
            if (tempY < -viewHeight) {
                tempY = -viewHeight
            } else if (tempY > deviceHeight) {
                tempY = deviceHeight
            }
            val imageScale = viewWidth.toFloat() / imageWidth.toFloat()
            val maxDy = abs((imageHeight * imageScale - viewHeight) * 0.5f)
            val translateY = -(2 * maxDy * tempY + maxDy * (viewHeight - deviceHeight)) /
                    (viewHeight + deviceHeight)
            canvas?.translate(0f, translateY)
        }
    }
}