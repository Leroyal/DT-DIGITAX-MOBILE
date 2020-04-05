package com.digitaltaxusa.framework.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.telephony.TelephonyManager
import android.text.util.Linkify
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.digitaltaxusa.framework.BuildConfig
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.logger.Logger
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object FrameworkUtils {
    private const val MINIMUM_PASSWORD_LENGTH = 6
    private const val DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"

    // click control threshold
    private const val CLICK_THRESHOLD = 300
    private var lastClickTime: Long = 0

    // tags
    // used for app debugging
    private const val TAG_MEMORY = "debug-memory"
    private const val TAG_INFO = "debug-info"

    // device attributes
    // used for app debugging
    private const val OS_VERSION = "os.version"

    /**
     * Method is used for printing the memory usage. This is used
     * only for verbosity mode
     *
     * @param name fragment or class simple name
     */
    fun printMemory(name: String) {
        if (Constants.DEBUG && Constants.DEBUG_VERBOSE) {
            val totalMemory = Runtime.getRuntime().totalMemory()
            val freeMemory = Runtime.getRuntime().freeMemory()
            val usedMemory = totalMemory - freeMemory
            // note that you cannot divide a long by a long value, this
            // refers to (long/long - long) operation giving a long result of 0
            val percentFree = (freeMemory.toFloat() / totalMemory * 100).toLong()
            val percentUsed = (usedMemory.toFloat() / totalMemory * 100).toLong()
            if (percentFree <= 2) {
                Logger.e(
                    TAG_MEMORY, "===== MEMORY WARNING :: Available memory is low! Please add " +
                            "'art-' to your regex tag to see that gc is freeing up more available memory ====="
                )
            }
            // printing memory details
            Logger.d(
                TAG_MEMORY, "===== Memory recorded from " + name + " :: " +
                        "MAX_MEMORY:" + Runtime.getRuntime().maxMemory() +
                        "  // FREE_MEMORY:" + freeMemory + " (" + percentFree + "% free)" +
                        "  // TOTAL_MEMORY:" + totalMemory +
                        "  // USED_MEMORY:" + usedMemory + " (" + percentUsed + "% used) ====="
            )
        }
    }

    /**
     * Method is used to print device and application information. This is
     * used only for verbosity mode
     *
     * @param context  Interface to global information about an application environment
     * @param activity The in-memory representation of a Java class
     */
    fun printInfo(context: Context, activity: Activity) {
        if (Constants.DEBUG && Constants.DEBUG_VERBOSE) {
            // determine phone carrier
            val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val carrierName = manager.networkOperatorName
            // get display metrics
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            try {
                Logger.i(
                    TAG_INFO, "===== DEVICE INFORMATION =====" +
                            "\nManufacturer: " + Build.MANUFACTURER +
                            "\nRegistrationModel: " + Build.MODEL +
                            "\nDevice/Product Id: " + Build.PRODUCT +
                            "\nCarrier: " + carrierName +
                            "\nOS Version: " + System.getProperty(OS_VERSION) +
                            "\nAPI Level: " + Build.VERSION.SDK_INT +
                            "\nScreen size (width/height): " +
                            displayMetrics.widthPixels + "/" +
                            displayMetrics.heightPixels +
                            "\n===== APP INFORMATION =====" +
                            "\nApp Version: " + BuildConfig.VERSION_NAME +
                            "\nBuild Type: " + BuildConfig.BUILD_TYPE +
                            "\nVersion Code: " + BuildConfig.VERSION_CODE +
                            "\nPackage Name: " + context.packageName +
                            "\nFlavor: " + BuildConfig.FLAVOR +
                            "\nGoogle Map API Version: " + context.packageManager
                        .getPackageInfo("com.google.android.apps.maps", 0).versionName
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Method is used to Linkify words in a TextView
     *
     * @param textView TextView who's text you want to change
     * @param linkThis A regex of what text to turn into a link
     * @param toThis   The url you want to send the user to
     */
    fun linkify(textView: TextView, linkThis: String, toThis: String) {
        val pattern = Pattern.compile(linkThis)
        Linkify.addLinks(textView, pattern, toThis, { _, _, _ -> true })
        { _, _ -> "" }
    }

    /**
     * Determine whether you have been granted a particular permission
     *
     * @param context        Interface to global information about an application environment
     * @param strPermissions The name of the permission being checked
     * @return True if permissions are enabled, otherwise false
     */
    fun checkAppPermissions(context: Context, vararg strPermissions: String?): Boolean {
        for (permissions in strPermissions) {
            if (permissions != null) {
                val result = ContextCompat.checkSelfPermission(context, permissions)
                if (result == PackageManager.PERMISSION_GRANTED) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Method is used to confirm that string parameter is in valid email format
     *
     * @param email Email of the user
     * @return True if email is valid format, otherwise false
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                email.substring(email.lastIndexOf(".") + 1).length > 1
    }

    /**
     * Method is used to confirm that a password was entered and is the correct length
     * [com.tatumgames.framework.utils.FrameworkUtils.MINIMUM_PASSWORD_LENGTH]
     *
     * @param password Password to confirm
     * @return True if password is the correct length
     */
    fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= MINIMUM_PASSWORD_LENGTH
    }

    /**
     * Method is used to get formatted date and time
     *
     * @return Current date and time
     */
    val currentDateTime: String
        get() {
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT, Locale.US)
            return formatter.format(calendar.time)
        }

    /**
     * Method is used to get formatted date and time in UTC
     *
     * @return Current date and time
     */
    val currentDateTimeUtc: String
        get() {
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT, Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(calendar.time)
        }

    /**
     * Method is used to get timezone
     *
     * @return Current time zone
     */
    val timeZone: String
        get() {
            val calendar = Calendar.getInstance()
            return calendar.timeZone.displayName
        }

    /**
     * Method is used to get formatted date and time
     *
     * @param dateFormat The format of the date
     * @return Current date and time
     */
    fun getCurrentDateTime(dateFormat: String): String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat(dateFormat, Locale.US)
        return formatter.format(calendar.time)
    }

    /**
     * Method is used to parse formatted date
     *
     * @param calendar   Calendar object [java.util.Calendar] with given date and time
     * @param dateFormat Method is used to parse formatted date
     * @return Formatted date and time
     */
    fun parseDateTime(calendar: Calendar, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.US)
        return formatter.format(calendar.time)
    }

    /**
     * Method is used to parse formatted date
     *
     * @param date       The date to parse
     * @param dateFormat Method is used to parse formatted date
     * @return Formatted date and time
     * @throws ParseException Thrown when the string being parsed is not in the correct form
     */
    @Throws(ParseException::class)
    fun parseDateTime(date: String, dateFormat: String): Date {
        val formatter = SimpleDateFormat(dateFormat, Locale.US)
        formatter.timeZone = TimeZone.getDefault()
        return formatter.parse(date)
    }

    /**
     * Method is used to parse day of the week
     *
     * @param calendar Calendar object [java.util.Calendar] with given date and time
     * @return Day of the week
     */
    fun parseDayOfTheWeek(calendar: Calendar): String {
        val date = calendar.time
        return SimpleDateFormat("EEEE", Locale.US).format(date.time)
    }

    /**
     * Method is used to convert date to another formatted date
     *
     * @param date       The date to parse
     * @param dateFormat Method is used to parse formatted date
     * @return The date string value converted from Date object
     */
    fun convertDateFormat(date: String, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.US)
        var dateObj: Date? = null
        try {
            dateObj = formatter.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (dateObj != null) {
            formatter.format(dateObj)
        } else ""
    }

    /**
     * Method is used to add set amount of minutes to current date; mm:ss
     *
     * @param minutesToAdd Minutes to add to current date and time
     * @return Calendar object [java.util.Calendar] with updated date and time
     */
    fun addMinutesToCurrentDate(minutesToAdd: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutesToAdd)
        return calendar
    }

    /**
     * Method is used to check if two calendar objects have the same day of year
     *
     * To be true, the year, day of month and day of the year must all be the same
     *
     * @param calendarA Calendar object [java.util.Calendar] with given date and time
     * @param calendarB Calendar object [java.util.Calendar] with given date and time
     * @return True if calendar objects have the same day of year
     */
    fun isSameDay(calendarA: Calendar, calendarB: Calendar): Boolean {
        return calendarA[Calendar.YEAR] == calendarB[Calendar.YEAR] &&
                calendarA[Calendar.DAY_OF_MONTH] == calendarB[Calendar.DAY_OF_MONTH] &&
                calendarA[Calendar.DAY_OF_YEAR] == calendarB[Calendar.DAY_OF_YEAR]
    }

    /**
     * Method is used to compare any date passed in as paramater to current date to see
     * which date-time combination is sooner or later
     *
     * @param dateTime String value representation of date and time
     * @return True if input date is after the current date
     */
    fun isDateAfterCurrentDate(dateTime: String): Boolean {
        try {
            val dateToCompare = parseDateTime(dateTime, DEFAULT_TIMESTAMP_FORMAT)
            val currentTime = Calendar.getInstance().time
            return dateToCompare.after(currentTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Method is used to compare any date passed in as paramater to current date to see
     * which date-time combination is sooner or later
     *
     * @param minDate    A specific moment in time, with millisecond precision
     * @param dateTime   String value representation of date and time
     * @param dateFormat Method is used to parse formatted date
     * @return True if input date is after the current date
     */
    fun isDateAfterCurrentDate(
        minDate: Date, dateTime: String,
        dateFormat: String
    ): Boolean {
        val formatter = SimpleDateFormat(
            if (dateFormat.isNotEmpty()) dateFormat else
                DEFAULT_TIMESTAMP_FORMAT, Locale.US
        )
        formatter.format(minDate.time)
        try {
            val parsedDate = parseDateTime(
                dateTime, if (dateFormat.isNotEmpty()) dateFormat else
                    DEFAULT_TIMESTAMP_FORMAT
            )
            return parsedDate.after(minDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Method is used to come two timestamps and determine the difference in days between the dates
     *
     * @param dateA String value representation of date and time
     * @param dateB String value representation of date and time
     * @return Number of days between two dates
     */
    fun getDaysBetweenDates(dateA: String, dateB: String): Int {
        val formatter = SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT, Locale.US)
        val startDate: Date
        val endDate: Date
        var numberOfDays: Long = 0
        try {
            startDate = formatter.parse(dateA) ?: Date()
            endDate = formatter.parse(dateB) ?: Date()
            val timeDiff: Long = endDate.time - startDate.time
            numberOfDays = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return numberOfDays.toInt()
    }

    /**
     * Method is used to convert double to dollar format
     *
     * @param value Value to convert to dollar format
     * @return Dollar formatted value
     */
    fun convertToDollarFormat(value: Double): String {
        val formatter = DecimalFormat("0.00")
        return formatter.format(value)
    }

    /**
     * Method is used to set visibility of views to VISIBLE
     *
     * @param params Views to set visibility to VISIBLE
     *
     * This class represents the basic building block for user interface components
     */
    fun setViewVisible(vararg params: View?) {
        for (v in params) {
            v?.visibility = View.VISIBLE
        }
    }

    /**
     * Method is used to set visibility of views to GONE
     *
     * @param params Views to set visibility to GONE
     *
     * This class represents the basic building block for user interface components
     */
    fun setViewGone(vararg params: View?) {
        for (v in params) {
            v?.visibility = View.GONE
        }
    }

    /**
     * Method is used to set visibility of views to INVISIBLE
     *
     * @param params Views to set visibility to INVISIBLE
     *
     * This class represents the basic building block for user interface components
     */
    fun setViewInvisible(vararg params: View?) {
        for (v in params) {
            v?.visibility = View.INVISIBLE
        }
    }

    /**
     * Method is used to confirm that string parameter is in valid area code format
     *
     * @param areaCode Area code to confirm
     * @return True if area code has valid format, otherwise false
     */
    fun isAreaCode(areaCode: String): Boolean {
        return areaCode.isNotEmpty() && areaCode.length >= 3 &&
                !areaCode.equals("000", ignoreCase = true) &&
                areaCode.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    /**
     * Method is used to confirm that string parameter is in valid zip code format
     *
     * @param zipCode Zip code to confirm
     * @return True if zip code has valid format, otherwise false
     */
    fun isZipCode(zipCode: String): Boolean {
        val zipCodePattern = "^\\d{5}(-\\d{4})?$".toRegex()
        return zipCode.matches(zipCodePattern)
    }

    /**
     * Method is used to determine if the provided String has a numeric value
     *
     * @param value String value to check
     * @return True if String value contains any numeric values, otherwise false
     */
    fun containsNumericValue(value: String): Boolean {
        return value.matches(".*\\d+.*".toRegex())
    }

    /**
     * Method is used to capitalize the first letter of any given string
     *
     * @param input String value to upper case first letter
     * @return The upper case equivalent for the specified character if the character
     * is a lower case letter
     */
    fun toTitleCase(input: String): String {
        if (input.isNotEmpty()) {
            val words = input.split(" ").toTypedArray()
            val sb = StringBuilder()
            for (w in words) {
                sb.append(Character.toUpperCase(w[0]))
                    .append(
                        w.substring(1)
                            .toLowerCase(Locale.US)
                    ).append(" ")
            }
            return sb.toString().trim { it <= ' ' }
        }
        return input
    }

    /**
     * Method is used to delay focus set on EditText view
     *
     * @param delay The delay (in milliseconds) until the Runnable will be executed
     * @param view  Views to request focus for
     *
     * This class represents the basic building block for user interface components
     */
    fun setFocusWithDelay(delay: Int, vararg view: View) {
        for (v in view) {
            val handler = Handler()
            handler.postDelayed({ v.requestFocus() }, delay.toLong())
        }
    }

    /**
     * Method is used to get color by id
     *
     * @param context Interface to global information about an application environment
     * @param id      The desired resource identifier, as generated by the aapt tool
     * @return A color integer associated with a particular resource ID
     */
    fun getColor(context: Context, id: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.getColor(context, id)
        } else {
            context.resources.getColor(id)
        }
    }

    /**
     * Method is used to get drawable by id
     *
     * @param context Interface to global information about an application environment
     * @param id      The desired resource identifier, as generated by the aapt tool
     * @return A drawable object associated with a particular resource ID
     */
    fun getDrawable(context: Context, id: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.getDrawable(context, id)
        } else {
            context.resources.getDrawable(id)
        }
    }

    /**
     * Method is used to control clicks on views. Clicking views repeatedly and quickly will
     * sometime cause crashes when objects and views are not fully animated or instantiated.
     * This helper method helps minimize and control UI interaction and flow
     *
     * @return True if view interaction has not been interacted with for set time
     */
    val isViewClickable: Boolean
        get() {
            /*
             * @Note: Android queues button clicks so it doesn't matter how fast or slow
             * your onClick() executes, simultaneous clicks will still occur. Therefore solutions
             * such as disabling button clicks via flags or conditions statements will not work.
             * The best solution is to timestamp the click processes and return back clicks
             * that occur within a designated window (currently 300 ms)
             */
            val mCurrClickTimestamp = SystemClock.uptimeMillis()
            val mElapsedTimestamp = mCurrClickTimestamp - lastClickTime
            lastClickTime = mCurrClickTimestamp
            return mElapsedTimestamp > CLICK_THRESHOLD
        }
}