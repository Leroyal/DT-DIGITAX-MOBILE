package com.digitaltaxusa.digitax.application

import android.app.Application
import com.digitaltaxusa.digitax.BuildConfig
import com.digitaltaxusa.digitax.api.configuration.DigitaxClientConfiguration
import com.digitaltaxusa.digitax.api.provider.DigitaxApiProvider
import com.digitaltaxusa.digitax.constants.UrlConstants
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.map.provider.GoogleServicesApiProvider
import com.digitaltaxusa.framework.sharedpref.SharedPref

class DigiTaxApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ////////////////////////////
        // API CLIENT
        ////////////////////////////
        // build client configuration required
        val clientConfiguration = DigitaxClientConfiguration.Builder()
            .setDebugModeEnabled(BuildConfig.DEBUG_MODE)
            .setBaseUrl(UrlConstants.BASE_URL)
            .create()

        // initialize DigitaxApiProvider
        DigitaxApiProvider.initialize(
            applicationContext,
            clientConfiguration
        )

        ////////////////////////////
        // GOOGLE SERVICE CLIENT
        ////////////////////////////
        GoogleServicesApiProvider.initialize(
            applicationContext
        )

        ////////////////////////////
        // FIREBASE
        ////////////////////////////
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