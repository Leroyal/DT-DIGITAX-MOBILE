package com.digitaltaxusa.digitax.activity

import android.os.Bundle
import com.digitaltaxusa.digitax.R


class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // initialize views and handlers
        initializeViews()
        initializeHandlers()
    }

    /**
     * Method is used to initialize views
     */
    private fun initializeViews() {
        // log screen event
        firebaseAnalyticsManager.logCurrentScreen(this, SplashActivity::class.java.simpleName)
    }

    /**
     * Method is used to initialize click listeners
     */
    private fun initializeHandlers() {

    }

}
