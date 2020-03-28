package com.digitaltaxusa.framework.constants

import com.digitaltaxusa.framework.BuildConfig

object Constants {
    // debuggable mode; true to see debug logs otherwise false
    const val DEBUG = BuildConfig.DEBUG_MODE
    // unique identifier
    const val ANDROID = "android"
    const val KEY_ANDROID_ID = "androidId"
    // shared pref
    const val PREF_FILE_NAME = "prefFileName"
}