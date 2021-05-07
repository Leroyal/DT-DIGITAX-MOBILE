package com.digitaltaxusa.digitax.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.activity.BaseActivity
import com.digitaltaxusa.digitax.databinding.FragmentForgotPasswordBinding
import com.digitaltaxusa.framework.device.DeviceUtils
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils

class ForgotPasswordFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: FragmentForgotPasswordBinding

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

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
        binding.header.tvHeader.text = resources.getString(R.string.forgot_password)
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
        binding.tvForgotPasswordCta.setOnClickListener(this)
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
                setCtaEnabled(FrameworkUtils.isValidEmail(s.toString()))
            }
        })
    }

    override fun onClick(v: View) {
        if (!FrameworkUtils.isViewClickable) {
            return
        }
        when (v.id) {
            R.id.iv_back -> {
                // remove fragment
                remove()
            }
            R.id.tv_forgot_password_cta -> {
                // TODO waiting on backend support
                // feature does not exist
                dialog.showOkDialog(
                    fragmentContext, resources.getString(R.string.dialog_title_product_feature),
                    resources.getString(R.string.dialog_feature_does_not_exist)
                )
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
        binding.tvForgotPasswordCta.isEnabled = isEnabled
        if (isEnabled) {
            binding.tvForgotPasswordCta.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.white
                )
            )
            binding.tvForgotPasswordCta.background =
                ContextCompat.getDrawable(fragmentContext, R.drawable.pill_purple_50_rad)
        } else {
            binding.tvForgotPasswordCta.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.black
                )
            )
            binding.tvForgotPasswordCta.background =
                ContextCompat.getDrawable(fragmentContext, R.drawable.pill_white_50_rad)
        }
    }

    override fun onResume() {
        super.onResume()
        // print memory

        val topFragment = (fragmentContext as BaseActivity).topFragment
        if (topFragment != null && topFragment.isVisible) {
            if (topFragment is ForgotPasswordFragment) {
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