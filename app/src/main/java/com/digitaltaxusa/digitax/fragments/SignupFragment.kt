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
import com.digitaltaxusa.digitax.databinding.FragmentSignupBinding
import com.digitaltaxusa.framework.device.DeviceUtils
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils

class SignupFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: FragmentSignupBinding

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

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
        // set header
        binding.header.tvHeader.text = resources.getString(R.string.sign_up)
        // request focus
        binding.edtEmail.requestFocus()

        // set CTA state
        setCtaEnabled(false)
    }

    /**
     * Method is used to initialize click listeners
     */
    private fun initializeHandlers() {
        binding.header.ivBack.setOnClickListener(this)
        binding.tvSignupCta.setOnClickListener(this)
        binding.tvShowPassword.setOnClickListener(this)
        binding.tvShowPasswordConfirm.setOnClickListener(this)
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
                            FrameworkUtils.isValidPassword(binding.edtPasswordConfirm.text.toString()) &&
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
                            FrameworkUtils.isValidPassword(binding.edtPasswordConfirm.text.toString()) &&
                            FrameworkUtils.isValidPassword(s.toString())
                )
            }
        })
        // password confirm editTextChangeListener
        binding.edtPasswordConfirm.addTextChangedListener(object : TextWatcher {
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
                            FrameworkUtils.isValidPassword(binding.edtPassword.text.toString()) &&
                            FrameworkUtils.isValidPassword(s.toString())
                )
            }
        })
        // password IME action listener
        binding.edtPassword.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.tvSignupCta.isEnabled) {
                binding.tvSignupCta.performClick()
                return@OnEditorActionListener true
            }
            false
        })
        // password confirm IME action listener
        binding.edtPasswordConfirm.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.tvSignupCta.isEnabled) {
                binding.tvSignupCta.performClick()
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
            R.id.tv_show_password_confirm -> {
                if (binding.tvShowPasswordConfirm.text.toString()
                        .equals(resources.getString(R.string.show), ignoreCase = true)
                ) {
                    binding.tvShowPasswordConfirm.setText(R.string.hide)
                    binding.edtPasswordConfirm.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.edtPasswordConfirm.setSelection(binding.edtPasswordConfirm.text.length)
                } else {
                    binding.tvShowPasswordConfirm.setText(R.string.show)
                    binding.edtPasswordConfirm.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.edtPasswordConfirm.setSelection(binding.edtPasswordConfirm.text.length)
                }
            }
            R.id.tv_signup_cta -> {
                signUp()
            }
            else -> {
                // do nothing
            }
        }
    }

    /**
     * Method is used to make /signin request
     */
    private fun signUp() {
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
        binding.tvSignupCta.isEnabled = isEnabled
        if (isEnabled) {
            binding.tvSignupCta.setTextColor(ContextCompat.getColor(fragmentContext, R.color.white))
            binding.tvSignupCta.background =
                ContextCompat.getDrawable(fragmentContext, R.drawable.pill_purple_50_rad)
        } else {
            binding.tvSignupCta.setTextColor(ContextCompat.getColor(fragmentContext, R.color.black))
            binding.tvSignupCta.background =
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