package com.digitaltaxusa.digitax.application

import android.app.Application
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager

class DigiTaxApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Room database
        FirebaseAnalyticsManager.getInstance(this)
    }
}