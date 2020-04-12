package com.digitaltaxusa.digitax.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.activity.BaseActivity
import com.digitaltaxusa.digitax.databinding.FragmentSigninBinding
import com.digitaltaxusa.framework.device.DeviceUtils
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils

class SigninFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: FragmentSigninBinding

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater, container, false)

        // instantiate views and listeners
        initializeViews()
        initializeHandlers()
        initializeListeners()

        return binding.root
    }

    /**
     * Method is used to initialize views
     */
    private fun initializeViews() {
        // log screen event
        firebaseAnalyticsManager.logCurrentScreen(
            fragmentActivity,
            SigninFragment::class.java.simpleName
        )
        // set header
        binding.header.tvHeader.text = resources.getString(R.string.sign_in)

        // set CTA state
        setCtaEnabled(false)
    }

    /**
     * Method is used to initialize click listeners
     */
    private fun initializeHandlers() {
        binding.header.ivBack.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.tvShowPassword.setOnClickListener(this)
        binding.tvSigninCta.setOnClickListener(this)
    }

    /**
     * Method is used to initialize listeners and callbacks
     */
    private fun initializeListeners() {
        // email editTextChangeListener
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //do nothing
            }

            override fun afterTextChanged(s: Editable) {
                // set CTA state
                setCtaEnabled(
                    FrameworkUtils.isValidEmail(s.toString()) &&
                            FrameworkUtils.isValidPassword(binding.edtPassword.text.toString())
                )
            }
        })
        // password editTextChangeListener
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                // set CTA state
                setCtaEnabled(
                    FrameworkUtils.isValidEmail(binding.edtEmail.text.toString()) &&
                            FrameworkUtils.isValidPassword(s.toString())
                )
            }
        })
        // password IME action listener
        binding.edtPassword.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.tvSigninCta.isEnabled) {
                binding.tvSigninCta.performClick()
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onClick(v: View) {
        if (!FrameworkUtils.isViewClickable) {
            return
        }
        when (v.id) {
            R.id.iv_back -> {
                remove()
                popBackStack()
            }
            R.id.tv_show_password -> {
                if (binding.tvShowPassword.text.toString()
                        .equals(resources.getString(R.string.show), ignoreCase = true)
                ) {
                    binding.tvShowPassword.setText(R.string.hide)
                    binding.edtPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.edtPassword.setSelection(binding.edtPassword.text.length)
                } else {
                    binding.tvShowPassword.setText(R.string.show)
                    binding.edtPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.edtPassword.setSelection(binding.edtPassword.text.length)
                }
            }
            R.id.tv_create_account -> {
                // add fragment
                addFragment(SignupFragment())
            }
            R.id.tv_forgot_password -> {
                // feature does not exist
                dialog.showDefaultOKAlert(
                    fragmentContext, resources.getString(R.string.product_feature),
                    resources.getString(R.string.feature_does_not_exist)
                )
            }
            R.id.tv_signin_cta -> {
                signIn()
            }
            else -> {
                // do nothing
            }
        }
    }

    /**
     * Method is used to make /signin request
     */
    private fun signIn() {
        // show progress dialog
        dialog.showProgressDialog(fragmentContext)
        // hide keyboard
        DeviceUtils.hideKeyboard(fragmentContext, fragmentActivity.window.decorView.windowToken)
    }

    /**
     * Method is used to enable / disable the Call To Action button
     *
     * @param isEnabled True if the CTA button should be enabled, otherwise false
     */
    private fun setCtaEnabled(isEnabled: Boolean) {
        binding.tvSigninCta.isEnabled = isEnabled
        if (isEnabled) {
            binding.tvSigninCta.setTextColor(ContextCompat.getColor(fragmentContext, R.color.white))
            binding.tvSigninCta.background =
                ContextCompat.getDrawable(fragmentContext, R.drawable.pill_purple_50_rad)
        } else {
            binding.tvSigninCta.setTextColor(ContextCompat.getColor(fragmentContext, R.color.black))
            binding.tvSigninCta.background =
                ContextCompat.getDrawable(fragmentContext, R.drawable.pill_white_50_rad)
        }
    }

    override fun onResume() {
        super.onResume()
        val topFragment = (fragmentContext as BaseActivity).topFragment
        if (topFragment != null && topFragment.isVisible) {
            if (topFragment is SigninFragment) {
                // show keyboard
                DeviceUtils.showKeyboard(fragmentContext)
            }
        }
    }

    override fun onPause() {
        // hide keyboard
        DeviceUtils.hideKeyboard(fragmentContext, fragmentActivity.window.decorView.windowToken)
        super.onPause()
    }
}