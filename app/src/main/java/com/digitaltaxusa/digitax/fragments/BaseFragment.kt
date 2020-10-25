package com.digitaltaxusa.digitax.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.utils.FrameworkUtils

open class BaseFragment : Fragment() {

    // context and activity
    // the [fragmentActivity] and [fragmentContext] are non-null [Activity] and [Context] objects
    protected lateinit var fragmentActivity: Activity
    protected lateinit var fragmentContext: Context

    // fire analytics
    protected lateinit var firebaseAnalyticsManager: FirebaseAnalyticsManager

    // onRemoveFragment listener
    private var onRemoveFragmentListener: OnRemoveFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize firebase analytics manager
        firebaseAnalyticsManager =
            FirebaseAnalyticsManager.getInstance(fragmentActivity.application)
    }

    /**
     * Set onRemoveListener used for inheritance
     *
     * @param fragment The Fragment to be removed
     */
    fun setOnRemoveListener(fragment: OnRemoveFragment) {
        onRemoveFragmentListener = fragment
    }

    /**
     * Method is used to pop the top state off the back stack. Returns true if there
     * was one to pop, else false. This function is asynchronous -- it enqueues the
     * request to pop, but the action will not be performed until the application
     * returns to its event loop.
     */
    fun popBackStack() {
        if (activity != null) {
            try {
                activity?.supportFragmentManager?.popBackStack()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Method is used to add fragment to the current stack
     *
     * @param fragment The new Fragment that is going to replace the container
     */
    fun addFragment(fragment: Fragment) {
        if (activity != null) {
            // check if the fragment has been added already
            val temp = activity?.supportFragmentManager?.findFragmentByTag(
                fragment.javaClass.simpleName
            )
            if (temp != null && temp.isAdded) {
                return
            }
            // track screen
            firebaseAnalyticsManager.logCurrentScreen(
                fragment.javaClass.simpleName
            )

            // add fragment and transition with animation
            activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(
                R.anim.ui_slide_in_from_bottom,
                R.anim.ui_slide_out_to_bottom, R.anim.ui_slide_in_from_bottom,
                R.anim.ui_slide_out_to_bottom
            )?.add(
                R.id.frag_container, fragment,
                fragment.javaClass.simpleName
            )?.addToBackStack(fragment.javaClass.simpleName)?.commit()
        }
    }

    /**
     * Method is used to add fragment to the current stack without animation
     *
     * @param fragment The new Fragment that is going to replace the container
     */
    fun addFragmentNoAnim(fragment: Fragment) {
        if (activity != null) {
            // check if the fragment has been added already
            val temp = activity?.supportFragmentManager?.findFragmentByTag(
                fragment.javaClass.simpleName
            )
            if (temp != null && temp.isAdded) {
                return
            }
            // track screen
            firebaseAnalyticsManager.logCurrentScreen(
                fragment.javaClass.simpleName
            )

            // add fragment and transition with animation
            activity?.supportFragmentManager?.beginTransaction()?.add(
                R.id.frag_container, fragment,
                fragment.javaClass.simpleName
            )?.addToBackStack(fragment.javaClass.simpleName)?.commit()
        }
    }

    /**
     * Method is used to add fragment with replace to stack without animation.
     * When Fragment is replaced all current fragments on the backstack are removed.
     *
     * @param fragment The Fragment to be added
     */
    fun addFragmentReplaceNoAnim(fragment: Fragment) {
        if (activity != null) {
            // check if the fragment has been added already
            val temp = activity?.supportFragmentManager?.findFragmentByTag(
                fragment.javaClass.simpleName
            )
            if (temp != null && temp.isAdded) {
                return
            }
            // track screen
            firebaseAnalyticsManager.logCurrentScreen(
                fragment.javaClass.simpleName
            )

            // replace fragment and transition
            try {
                if (topFragment != null && topFragment?.tag?.isNotEmpty() == true &&
                    topFragment?.isAdded == true
                ) {
                    // pop back stack
                    popBackStack()
                }
                activity?.supportFragmentManager?.beginTransaction()?.replace(
                    R.id.frag_container, fragment,
                    fragment.javaClass.simpleName
                )?.addToBackStack(fragment.javaClass.simpleName)?.commit()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                // used as last resort
                activity?.supportFragmentManager?.beginTransaction()?.replace(
                    R.id.frag_container, fragment,
                    fragment.javaClass.simpleName
                )?.addToBackStack(fragment.javaClass.simpleName)?.commitAllowingStateLoss()
            }
        }
    }

    /**
     * Method for removing the Fragment view
     */
    fun remove() {
        try {
            if (activity != null) {
                val ft = activity?.supportFragmentManager?.beginTransaction()
                ft?.setCustomAnimations(
                    R.anim.ui_slide_in_from_bottom,
                    R.anim.ui_slide_out_to_bottom
                )
                ft?.remove(this)?.commitAllowingStateLoss()
                activity?.supportFragmentManager?.popBackStack()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * Method for removing the Fragment view with no animation
     */
    fun removeNoAnim() {
        if (activity != null) {
            try {
                val ft = activity?.supportFragmentManager?.beginTransaction()
                ft?.remove(this)?.commitAllowingStateLoss()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Method is used to retrieve the current fragment the user is on
     *
     * @return Returns the TopFragment if there is one, otherwise returns null
     */
    private val topFragment: Fragment?
        get() {
            if (activity != null) {
                val backStackEntryCount = activity?.supportFragmentManager?.backStackEntryCount ?: 0
                if (backStackEntryCount > 0) {
                    var i = backStackEntryCount
                    while (i >= 0) {
                        i--
                        val topFragment = activity?.supportFragmentManager?.fragments?.get(i)
                        if (topFragment != null) {
                            return topFragment
                        }
                    }
                }
            }
            return null
        }

    /**
     * Method is used to re-direct to a different Activity with no transition
     *
     * @param clazz         The in-memory representation of a Java class
     * @param intent           An intent is an abstract description of an operation to be performed
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
        val i = intent ?: Intent(fragmentContext, clazz)
        if (isClearBackStack == true) {
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        } else {
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (!fragmentActivity.isFinishing) {
            // track screen
            firebaseAnalyticsManager.logCurrentScreen(clazz.javaClass.simpleName)

            // start activity
            startActivity(i)
        }
    }

    /**
     * Method is used to re-direct to different Activity from a fragment with a
     * transition animation slide in from bottom of screen
     *
     * @param clazz         The in-memory representation of a Java class
     * @param intent           An intent is an abstract description of an operation to be performed
     * @param isClearBackStack If set in an Intent passed to Context.startActivity(),
     * this flag will cause any existing task that would be associated
     * with the activity to be cleared before the activity is started
     */
    fun goToActivityAnimInFromBottom(
        clazz: Class<*>,
        intent: Intent?,
        isClearBackStack: Boolean? = true
    ) {
        // set intent
        val i = intent ?: Intent(fragmentContext, clazz)
        if (isClearBackStack == true) {
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        } else {
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (!fragmentActivity.isFinishing) {
            // track screen
            firebaseAnalyticsManager.logCurrentScreen(clazz.javaClass.simpleName)

            // start activity
            startActivity(i)
            // transition animation
            fragmentActivity.overridePendingTransition(
                R.anim.ui_slide_in_from_bottom,
                R.anim.ui_slide_out_to_bottom
            )
        }
    }

    /**
     * Method is used to re-direct to different Activity from a fragment with a
     * transition animation slide in from bottom of screen
     *
     * @param clazz         The in-memory representation of a Java class
     * @param intent           An intent is an abstract description of an operation to be performed
     * @param isClearBackStack If set in an Intent passed to Context.startActivity(),
     * this flag will cause any existing task that would be associated
     * with the activity to be cleared before the activity is started
     */
    fun goToActivityAnimInFromTop(
        clazz: Class<*>,
        intent: Intent?,
        isClearBackStack: Boolean? = true
    ) {
        // set intent
        val i = intent ?: Intent(fragmentContext, clazz)
        if (isClearBackStack == true) {
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        } else {
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (!fragmentActivity.isFinishing) {
            // track screen
            firebaseAnalyticsManager.logCurrentScreen(clazz.javaClass.simpleName)

            // start activity
            startActivity(i)
            // transition animation
            fragmentActivity.overridePendingTransition(
                R.anim.ui_slide_in_from_top,
                R.anim.ui_slide_out_to_top
            )
        }
    }

    override fun onResume() {
        super.onResume()
        FrameworkUtils.printMemory(fragmentActivity.javaClass.simpleName)
    }

    override fun onDetach() {
        super.onDetach()
        if (onRemoveFragmentListener != null) {
            onRemoveFragmentListener?.onRemove()
            onRemoveFragmentListener = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
        if (fragmentContext is Activity) {
            fragmentActivity = fragmentContext as Activity
        }
    }

    /**
     * Method for removing a fragment
     */
    interface OnRemoveFragment {
        fun onRemove()
    }
}