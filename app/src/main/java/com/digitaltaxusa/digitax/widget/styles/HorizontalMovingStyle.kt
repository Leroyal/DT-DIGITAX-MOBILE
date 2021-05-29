package com.digitaltaxusa.digitax.widget.styles

import android.graphics.Canvas
import android.widget.ImageView
import com.digitaltaxusa.digitax.widget.ParallaxImageView
import kotlin.math.abs

/**
 * When the imageView is scrolling horizontally, the image in imageView will also
 * scroll horizontally if the image width is larger than the imageView width.
 *
 * Note: The image will not over scroll to it's view bounds.
 *
 * <p>Only supports imageView with CENTER_CROP scale type.</p>
 */
class HorizontalMovingStyle : ParallaxImageView.ParallaxStyle {

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
        var tempX = x
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

        // device width
        val deviceWidth: Int = view.resources.displayMetrics.widthPixels
        if (imageWidth * viewHeight > imageHeight * viewWidth) {
            // avoid over scroll
            if (x < -viewWidth) {
                tempX = -viewWidth
            } else if (x > deviceWidth) {
                tempX = deviceWidth
            }
            val imageScale = viewHeight.toFloat() / imageHeight.toFloat()
            val maxDx = abs((imageWidth * imageScale - viewWidth) * 0.5f)
            val translateX = -(2 * maxDx * tempX + maxDx * (viewWidth - deviceWidth)) /
                    (viewWidth + deviceWidth)
            canvas?.translate(translateX, 0f)
        }
    }
}