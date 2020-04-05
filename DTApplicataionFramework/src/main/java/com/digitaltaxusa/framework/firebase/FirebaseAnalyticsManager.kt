package com.digitaltaxusa.framework.firebase

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

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
         * @param application Application Base class for maintaining global application state.
         * @return FirebaseAnalyticsManager
         */
        fun getInstance(application: Application): FirebaseAnalyticsManager {
            // all synchronized blocks synchronized on the same object can only have
            // one thread executing inside them at a time. All other threads attempting
            // to enter the synchronized block are blocked until the thread inside the
            // synchronized block exits the block
            synchronized(FirebaseAnalyticsManager::class.java) {
                firebaseAnalytics = FirebaseAnalytics.getInstance(application.applicationContext)
                INSTANCE = FirebaseAnalyticsManager()
            }
            return INSTANCE
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
     * @param key String
     * @param bundle Bundle A mapping from String keys to various Parcelable values.
     */
    fun logEvent(
        key: String,
        bundle: Bundle
    ) {
        firebaseAnalytics.logEvent(key, bundle)
    }

    /**
     * Manually set the screen name and optionally override the class name when screen
     * transitions occur. After setting the screen name, events that occur on these screens
     * are additionally tagged with the parameter firebase_screen. For example, you could
     * name a screen "Main Menu" or "Friends List".
     *
     * <p>Source: https://firebase.google.com/docs/analytics/screenviews</p>
     *
     * @param activity Activity An activity is a single, focused thing that the user can do.
     * @param T Any The root of the Kotlin class hierarchy. Every Kotlin class has Any as a superclass.
     */
    fun logCurrentScreen(
        activity: Activity,
        T: Any
    ) {
        firebaseAnalytics.setCurrentScreen(
            activity,
            T::class.java.simpleName,
            null /* class override */
        )
    }
}