package com.digitaltaxusa.digitax.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    // initialize fragment manager
    protected var fragmentManager: FragmentManager = supportFragmentManager

    // fire analytics
    protected lateinit var firebaseAnalyticsManager: FirebaseAnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // disable screenshots
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )
        // initialize firebase analytics manager
        firebaseAnalyticsManager = FirebaseAnalyticsManager.getInstance(this.application)
    }

    /**
     * Method is used to add fragment to the current stack
     *
     * @param fragment The new Fragment that is going to replace the container
     */
    fun addFragment(fragment: Fragment) {
        // check if the fragment has been added already
        val temp = fragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
        if (temp != null) {
            return
        }
        // track screen
        firebaseAnalyticsManager.logCurrentScreen(fragment.javaClass.simpleName)

        // add fragment and transition with animation
        fragmentManager.beginTransaction().setCustomAnimations(
            R.anim.ui_slide_in_from_bottom,
            R.anim.ui_slide_out_to_bottom, R.anim.ui_slide_in_from_bottom,
            R.anim.ui_slide_out_to_bottom
        ).add(
            R.id.frag_container, fragment,
            fragment.javaClass.simpleName
        ).addToBackStack(null).commit()
    }

    /**
     * Method is used to retrieve the current fragment the user is on
     *
     * @return Returns the TopFragment if there is one, otherwise returns null
     */
    val topFragment: Fragment?
        get() {
            return fragmentManager.fragments[0]
        }

    /**
     * Method is used to re-direct to a different Activity with no transition
     *
     * @param clazz The in-memory representation of a Java class
     * @param intent An intent is an abstract description of an operation to be performed
     * @param isClearBackStack If set in an Intent passed to Context.startActivity(),
     * this flag will cause any existing task that would be associated
     * with the activity to be cleared before the activity is started
     */
    fun goToActivity(
        clazz: Class<*>,
        intent: Intent?,
        isClearBackStack: Boolean? = true
    ) {
        // set intent
        val i = intent ?: Intent(this, clazz)
        if (isClearBackStack == true) {
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        } else {
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (!isFinishing) {
            // track screen
            firebaseAnalyticsManager.logCurrentScreen(clazz.javaClass.simpleName)

            // start activity
            startActivity(i)
        }
    }
}