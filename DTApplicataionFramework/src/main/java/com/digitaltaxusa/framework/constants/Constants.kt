package com.digitaltaxusa.framework.constants

import com.digitaltaxusa.framework.BuildConfig

object Constants {
    const val TAG = "DigiTax"

    // debuggable mode; true to see debug logs otherwise false
    const val DEBUG = BuildConfig.DEBUG_MODE

    // verbose mode; true to see verbosity mode otherwise false
    const val DEBUG_VERBOSE = true

    // shared pref
    const val PREF_FILE_NAME = "prefFileName"
}