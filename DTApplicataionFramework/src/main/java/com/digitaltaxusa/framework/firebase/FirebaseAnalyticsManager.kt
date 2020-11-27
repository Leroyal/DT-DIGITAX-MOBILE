package com.digitaltaxusa.framework.firebase

import android.content.Context
import android.os.Bundle
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager.Params.Companion.BACK_END_ERROR
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager.Params.Companion.FRONT_END_ERROR
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager.Params.Companion.HANDLED_ERROR
import com.digitaltaxusa.framework.http.response.ErrorItem
import com.digitaltaxusa.framework.logger.Logger
import com.google.firebase.analytics.FirebaseAnalytics

private const val TAG = "FirebaseAnalyticsManager"

class FirebaseAnalyticsManager {

    companion object {
        @Volatile
        private lateinit var INSTANCE: FirebaseAnalyticsManager
        private lateinit var firebaseAnalytics: FirebaseAnalytics

        /**
         * Firebase analytics manager to capture a number of events and user properties and
         * also allows to define custom events.
         * <p>Source: https://firebase.google.com/docs/analytics</p>
         *
         * @param context Interface to global information about an application environment.
         * @return FirebaseAnalyticsManager
         */
        fun getInstance(context: Context): FirebaseAnalyticsManager {
            // all synchronized blocks synchronized on the same object can only have
            // one thread executing inside them at a time. All other threads attempting
            // to enter the synchronized block are blocked until the thread inside the
            // synchronized block exits the block
            synchronized(FirebaseAnalyticsManager::class.java) {
                firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
                INSTANCE = FirebaseAnalyticsManager()
            }
            return INSTANCE
        }
    }

    /**
     * Preset events to be used in the app
     *
     * Event Keys:
     * APP_INSTALL: N/A - custom event
     * ERROR: N/A - custom event
     * APP_OPEN: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-app_open
     * SIGN_IN: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-login
     * SIGN_UP: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-sign_up
     * SEARCH: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-search
     * SHARE: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-share
     * TUTORIAL_BEGIN: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-tutorial_begin
     * TUTORIAL_COMPLETE: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-tutorial_complete
     * VIEW_SEARCH_RESULTS: https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-view_search_results
     */
    class Event {
        companion object {
            const val APP_INSTALL = "app_install"
            const val ERROR = "error"
            const val APP_OPEN = FirebaseAnalytics.Event.APP_OPEN
            const val SIGN_IN = FirebaseAnalytics.Event.LOGIN
            const val SIGN_UP = FirebaseAnalytics.Event.SIGN_UP
            const val SEARCH = FirebaseAnalytics.Event.SEARCH
            const val SHARE = FirebaseAnalytics.Event.SHARE
            const val TUTORIAL_BEGIN = FirebaseAnalytics.Event.TUTORIAL_BEGIN
            const val TUTORIAL_COMPLETE = FirebaseAnalytics.Event.TUTORIAL_COMPLETE
            const val VIEW_SEARCH_RESULTS = FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS
        }
    }

    /**
     * Preset params to be used in the app
     *
     * <p>Param keys are extra properties added to [Event]. There properties are custom and
     * are used to add further details about requests, exceptions and errors.</p>
     */
    class Params {
        companion object {
            const val REQUEST = "request"
            const val FRONT_END_ERROR = "front_end_error"
            const val BACK_END_ERROR = "back_end_error"
            const val HANDLED_ERROR = "handled_error"
            const val UNHANDLED_ERROR = "unhandled_error"
        }
    }

    /**
     * Events provide insight on what is happening in your app, such as user actions,
     * system events, or errors.
     *
     * <p>If your app needs to collect additional data, you can log up to 500 different
     * Analytics Event types in your app. There is no limit on the total volume of events
     * your app logs. Note that event names are case-sensitive and that logging two events
     * whose names differ only in case will result in two distinct events.
     *
     * Source: https://firebase.google.com/docs/analytics/events</p>
     *
     * @param key String The event. An Event is an important occurrence in your app
     * that you want to measure.
     * @param bundle Bundle A mapping from String keys to various Parcelable values.
     */
    fun logEvent(
        key: String,
        bundle: Bundle? = null
    ) {
        Logger.i(TAG, "Logging event $key")
        firebaseAnalytics.logEvent(key, bundle)
    }

    /**
     * Track failed API requests.
     *
     * <p>These are handled failed HTTP requests. Handled means that an action is taken in code
     * when the specific request fails. This can be a retry, bubbled up messaging, ect.</p>
     *
     * @param errorItem ErrorItem Distinguishes between a runtime error and a failed HTTP response.
     */
    fun logApiException(
        errorItem: ErrorItem
    ) {
        Logger.i(TAG, "Logging API error ${errorItem.exception}")

        // create bundle
        val bundle = Bundle()
        bundle.putString(BACK_END_ERROR, errorItem.toString())

        // log event
        firebaseAnalytics.logEvent(HANDLED_ERROR, bundle)
    }

    /**
     * Track non-API exception errors.
     *
     * <p>These are unexpected exceptions that occur, that are also anticipated. Meaning that
     * the exception is handled. This can be additional code after the exception to retry, course
     * correct, transition, take an alternative route, ect.</p>
     *
     * @param errorItem ErrorItem Distinguishes between a runtime error and a failed HTTP response.
     */
    fun logNonApiException(
        errorItem: ErrorItem
    ) {
        Logger.i(TAG, "Logging non-API error ${errorItem.exception}")

        // create bundle
        val bundle = Bundle()
        bundle.putString(FRONT_END_ERROR, errorItem.toString())

        // log event
        firebaseAnalytics.logEvent(HANDLED_ERROR, bundle)
    }

    /**
     * Manually set the screen name and optionally override the class name when screen
     * transitions occur. After setting the screen name, events that occur on these screens
     * are additionally tagged with the parameter firebase_screen. For example, you could
     * name a screen "Main Menu" or "Friends List".
     *
     * <p>Source: https://firebase.google.com/docs/analytics/screenviews</p>
     *
     * @param screenName Custom screen name. Currently using `simple name` of the underlying
     * class as given in the source code.
     */
    fun logCurrentScreen(
        screenName: String
    ) {
        Logger.i(TAG, "Logging screen $screenName")

        // create bundle
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)

        // log event
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}