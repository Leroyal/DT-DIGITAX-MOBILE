package com.digitaltaxusa.digitax.constants

object FirebaseAnalyticsConstants {

    // EVENTS - https://firebase.google.com/docs/analytics/events
    // http errors and exceptions
    const val HTTP_ERROR = "error_http" // log error response and message
    const val EXCEPTION = "error_exception" // log exception error and message

    // action events
    const val ACTION_APP_OPEN = "action_app_open"
}