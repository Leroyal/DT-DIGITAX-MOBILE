package com.digitaltaxusa.digitax.application

import android.app.Application
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.sharedpref.SharedPref

class DigiTaxApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize and get instance for [FirebaseAnalyticsManager]
        val firebaseAnalyticsManager = FirebaseAnalyticsManager.getInstance(this)

        // track when user installs application
        val pref = SharedPref(applicationContext, Constants.PREF_FILE_NAME)
        if (!pref.getBooleanPref(FirebaseAnalyticsManager.Event.APP_INSTALL, false)) {
            pref.setPref(FirebaseAnalyticsManager.Event.APP_INSTALL, true)
            firebaseAnalyticsManager.logEvent(FirebaseAnalyticsManager.Event.APP_INSTALL)
        }
        // track when user opens application
        firebaseAnalyticsManager.logEvent(FirebaseAnalyticsManager.Event.APP_OPEN)
    }
}