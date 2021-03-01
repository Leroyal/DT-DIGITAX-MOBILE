package com.digitaltaxusa.framework.logger

import android.util.Log
import com.digitaltaxusa.framework.constants.Constants

object Logger {

    /**
     * Helper method for logging e-verbose.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param message The message you would like log.
     */
    fun e(tag: String, message: String?) {
        if (Constants.DEBUG) {
            Log.e(tag, message.orEmpty())
        }
    }

    /**
     * Helper method for logging e-verbose.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param message The message you would like log.
     * @param exception Exception is the superclass of all classes that represent recoverable exceptions.
     */
    fun e(tag: String, message: String?, exception: Exception) {
        if (Constants.DEBUG) {
            Log.e(tag, message.orEmpty(), exception)
        }
    }

    /**
     * Helper method for logging d-verbose.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param message The message you would like log.
     */
    fun d(tag: String, message: String?) {
        if (Constants.DEBUG) {
            Log.d(tag, message.orEmpty())
        }
    }

    /**
     * Helper method for logging i-verbose.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param message The message you would like log.
     */
    fun i(tag: String, message: String?) {
        if (Constants.DEBUG) {
            Log.i(tag, message.orEmpty())
        }
    }

    /**
     * Helper method for logging v-verbose.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param message The message you would like log.
     */
    fun v(tag: String, message: String?) {
        if (Constants.DEBUG) {
            Log.v(tag, message.orEmpty())
        }
    }

    /**
     * Helper method for logging w-verbose.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param message The message you would like log.
     */
    fun w(tag: String, message: String?) {
        if (Constants.DEBUG) {
            Log.w(tag, message.orEmpty())
        }
    }

    /**
     * Helper method to display data on Console.
     *
     * @param message The message to be displayed.
     */
    fun printOnConsole(message: String) {
        if (Constants.DEBUG) {
            println(message)
        }
    }
}