package com.digitaltaxusa.framework.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout

/**
 * A user interface element that indicates the progress of an operation.
 * Progress bar supports two modes to represent progress: determinate,
 * and indeterminate. For a visual overview of the difference between
 * determinate and indeterminate progress modes.
 *
 * @param context Interface to global information about an application environment
 */
class ProgressBarUtils(private val context: Context) {

    private val progressBar: ProgressBar?

    init {
        val layout = (context as Activity).findViewById<View>(
            android.R.id.content
        ).rootView as ViewGroup
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyle)
        // change the indeterminate mode for this progress bar
        progressBar.isIndeterminate = true

        // set params and attributes of RelativeLayout container
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        val rlContainer = RelativeLayout(context)
        rlContainer.gravity = Gravity.CENTER
        rlContainer.addView(progressBar)
        layout.addView(rlContainer, params)

        // set default visibility state
        showLoading(false)
    }

    /**
     * Method is used to show progress bar.
     *
     * @param isShow True if showing progress bar, otherwise false.
     */
    fun showLoading(isShow: Boolean) {
        if (progressBar == null) {
            return
        }
        // set visibility
        progressBar.visibility = if (isShow) {
            disableBackgroundClicks()
            View.VISIBLE
        } else {
            enableBackgroundClicks()
            View.GONE
        }
    }

    /**
     * Method used to disable background clicks.
     */
    private fun disableBackgroundClicks(): Unit =
        (context as Activity).window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

    /**
     * Method used to enable background clicks.
     */
    private fun enableBackgroundClicks(): Unit =
        (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}
