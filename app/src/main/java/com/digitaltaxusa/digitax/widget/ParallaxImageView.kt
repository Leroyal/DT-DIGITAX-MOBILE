package com.digitaltaxusa.digitax.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView

/**
 * Custom image view that scrolls and operates like a parallax background in adapters.
 *
 * @property viewLocation IntArray Set [IntArray] that tracks view position.
 * @property enableScrollParallax Boolean True to enable parallax scrolling animation,
 * otherwise false.
 * @property parallaxStyles ParallaxStyle? Interface to set parallax style.
 * @constructor
 */
class ParallaxImageView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : AppCompatImageView(
    context,
    attrs,
    defStyleAttr
), ViewTreeObserver.OnScrollChangedListener {

    private val viewLocation = IntArray(2)
    private var enableScrollParallax = true
    private var parallaxStyles: ParallaxStyle? = null

    // constructors
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * Implement this to do your drawing.
     *
     * @param canvas Canvas The Canvas class holds the "draw" calls. To draw something,
     * you need 4 basic components: A Bitmap to hold the pixels, a Canvas to host the
     * draw calls (writing into the bitmap), a drawing primitive (e.g. Rect, Path, text, Bitmap),
     * and a paint (to describe the colors and styles for the drawing).
     */
    override fun onDraw(canvas: Canvas?) {
        if (!enableScrollParallax || drawable == null) {
            super.onDraw(canvas)
            return
        }
        if (parallaxStyles != null) {
            getLocationInWindow(viewLocation)
            parallaxStyles?.transform(this, canvas, viewLocation[0], viewLocation[1])
        }
        super.onDraw(canvas)
    }

    /**
     * This is called when the view is attached to a window. At this point it has a Surface
     * and will start drawing.
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnScrollChangedListener(this)
    }

    /**
     * This is called when the view is detached from a window. At this point it no longer
     * has a surface for drawing.
     */
    override fun onDetachedFromWindow() {
        viewTreeObserver.removeOnScrollChangedListener(this)
        super.onDetachedFromWindow()
    }

    /**
     * Callback method to be invoked when something in the view tree has been scrolled.
     */
    override fun onScrollChanged() {
        if (enableScrollParallax) {
            // invalidate the whole view. If the view is visible, onDraw(Canvas) will be
            // called at some point in the future.
            invalidate()
        }
    }

    /**
     * Method is used to set the parallax style.
     *
     * @param styles ParallaxStyle? Interface to set parallax style.
     */
    fun setParallaxStyles(styles: ParallaxStyle?) {
        if (parallaxStyles != null) {
            // before attaching style to another imageView, remove current attached style
            parallaxStyles?.onDetachedFromImageView(this)
        }
        // set styles
        parallaxStyles = styles
        // attach style to imageView
        parallaxStyles?.onAttachedToImageView(this)
    }

    /**
     * Method is used to control scrolling properties.
     *
     * @param enableScrollParallax Boolean True to enable parallax scrolling animation,
     * otherwise false.
     */
    fun setEnableScrollParallax(enableScrollParallax: Boolean) {
        this.enableScrollParallax = enableScrollParallax
    }

    interface ParallaxStyle {
        fun onAttachedToImageView(view: ParallaxImageView?)
        fun onDetachedFromImageView(view: ParallaxImageView?)
        fun transform(view: ParallaxImageView?, canvas: Canvas?, x: Int, y: Int)
    }
}