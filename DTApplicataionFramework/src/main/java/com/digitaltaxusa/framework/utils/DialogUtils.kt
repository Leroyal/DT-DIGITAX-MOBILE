package com.digitaltaxusa.framework.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.digitaltaxusa.framework.R
import com.digitaltaxusa.framework.network.NetworkUtils

class DialogUtils {
    private var dialog: Dialog? = null
    private var networkDialog: AlertDialog? = null

    // display during processing requests/responses
    private var progressBar: ProgressBar? = null

    // click listener for default dialog
    private val defaultListener = DialogInterface.OnClickListener { _, _ -> dismissDialog() }

    /**
     * Method is used to dismiss dialog
     */
    fun dismissDialog() {
        try {
            if (dialog != null && dialog?.isShowing == true) {
                dialog?.dismiss()
                dialog = null
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * Method is used to dismiss progress dialog
     */
    fun dismissProgressDialog() {
        try {
            if (progressBar != null && progressBar?.isVisible == true) {
                progressBar?.visibility = View.GONE
                progressBar = null
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * Method is used to dismiss no network dialog
     */
    fun dismissNoNetworkDialog() {
        try {
            if (networkDialog != null && networkDialog?.isShowing == true) {
                networkDialog?.dismiss()
                networkDialog = null
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * @param context Interface to global information about an application environment
     * @param title   The displayed title
     * @param message The displayed message
     */
    fun showNoNetworkDialog(
        context: Context,
        title: String?,
        message: String?
    ) {
        if ((context as Activity).isFinishing ||
            networkDialog != null && networkDialog?.isShowing == true
        ) {
            return
        }
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(context.resources.getString(R.string.retry), null)
        // create builder and set equal to dialog
        networkDialog = builder.create()
        networkDialog?.show()

        // set listener
        networkDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            if (NetworkUtils.isConnected(context)) {
                dismissNoNetworkDialog()
            }
        }
    }

    /**
     * Dialog constructor
     *
     * @param context   Interface to global information about an application environment
     * @param title     The displayed title
     * @param message   The displayed message
     * @param listener  Interface used to allow the creator of a dialog to run some code
     * when an item on the dialog is clicked
     */
    fun showOkDialog(
        context: Context,
        title: String?,
        message: String?,
        listener: DialogInterface.OnClickListener? = defaultListener
    ) {
        if ((context as Activity).isFinishing ||
            dialog != null && dialog?.isShowing == true
        ) {
            return
        }
        // creates a builder for an alert dialog that uses the default alert dialog theme
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(context.resources.getString(R.string.ok), listener)
        // create builder and set equal to dialog
        dialog = builder.create()
        dialog?.show()
    }

    /**
     * Method is used to construct dialog with a message, and both negative and positive buttons
     *
     * @param context     Interface to global information about an application environment
     * @param message     The displayed message
     * @param yesText     The text to display on the positive button
     * @param noText      The text to display on the negative button
     * @param yesListener Interface used to allow the creator of a dialog to run some code
     * when an item on the dialog is clicked
     * @param noListener  Interface used to allow the creator of a dialog to run some code
     * when an item on the dialog is clicked
     */
    fun showYesNoDialog(
        context: Context,
        title: String?,
        message: String?,
        yesText: String?,
        noText: String?,
        yesListener: DialogInterface.OnClickListener? = defaultListener,
        noListener: DialogInterface.OnClickListener? = defaultListener
    ) {
        if ((context as Activity).isFinishing ||
            dialog != null && dialog?.isShowing == true
        ) {
            return
        }
        // set positive/negative button values
        val yes = if (yesText?.isNotEmpty() == true) yesText else
            context.getResources().getString(R.string.yes)
        val no = if (noText?.isNotEmpty() == true) noText else
            context.getResources().getString(R.string.no)
        // creates a builder for an alert dialog that uses the default alert dialog theme
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(yes, yesListener)
            .setNegativeButton(no, noListener)
        // create builder and set equal to dialog
        dialog = builder.create()
        dialog?.show()
    }

    /**
     * Method is used to display progress dialog. Call when processing requests/responses
     *
     * @param context Interface to global information about an application environment
     */
    fun showProgressDialog(
        context: Context
    ) {
        if ((context as Activity).isFinishing ||
            (progressBar != null && progressBar?.isVisible == true)
        ) {
            return
        }
        progressBar = ProgressBar(context, null, R.attr.progressBarStyle)
        // change the indeterminate mode for this progress bar
        progressBar?.isIndeterminate = true
        progressBar?.visibility = View.VISIBLE
    }

    /**
     * Method is used to construct dialog with an error message. Intended to be used for
     * HTTP request failures.
     *
     * @param context   Interface to global information about an application environment
     * @param message   The displayed message
     * @param listener  Interface used to allow the creator of a dialog to run some code
     * when an item on the dialog is clicked
     */
    fun createErrorDialog(
        context: Context,
        message: String?,
        listener: DialogInterface.OnClickListener? = defaultListener
    ) {
        if ((context as Activity).isFinishing ||
            dialog != null && dialog?.isShowing == true
        ) {
            return
        }
        // creates a builder for an alert dialog that uses the default alert dialog theme
        val builder = AlertDialog.Builder(context)
            .setTitle(context.resources.getString(R.string.error))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(context.getResources().getString(R.string.ok), listener)
        // create builder and set equal to dialog
        dialog = builder.create()
        dialog?.show()
    }
}