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
import com.digitaltaxusa.digitax.activity.MainActivity
import com.digitaltaxusa.digitax.api.client.DigitaxApiInterface
import com.digitaltaxusa.digitax.api.provider.DigitaxApiProvider
import com.digitaltaxusa.digitax.api.requests.SigninRequest
import com.digitaltaxusa.digitax.api.response.SigninResponse
import com.digitaltaxusa.digitax.constants.Constants
import com.digitaltaxusa.digitax.databinding.FragmentSigninBinding
import com.digitaltaxusa.framework.device.DeviceUtils
import com.digitaltaxusa.framework.http.response.Response
import com.digitaltaxusa.framework.http.response.ResponseCallback
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils
import com.digitaltaxusa.framework.utils.getErrorMessage

class SigninFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: FragmentSigninBinding

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    // api client and configuration
    private val digitaxApiClient: DigitaxApiInterface = DigitaxApiProvider.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        // set header
        binding.header.tvHeader.text = resources.getString(R.string.sign_in)
        // request focus
        binding.edtEmailUsername.requestFocus()

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
        binding.tvCreateAccount.setOnClickListener(this)
        binding.tvSigninCta.setOnClickListener(this)
    }

    /**
     * Method is used to initialize listeners and callbacks
     */
    private fun initializeListeners() {
        // email editTextChangeListener
        binding.edtEmailUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //do nothing
            }

            override fun afterTextChanged(s: Editable) {
                // set CTA state
                setCtaEnabled(
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
                // remove fragment
                remove()
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
                // add fragment
                addFragment(ForgotPasswordFragment())
            }
            R.id.tv_signin_cta -> {
                signin()
            }
        }
    }

    /**
     * Method is used to make /api/auth/signin request
     */
    private fun signin() {
        // show progress dialog
        dialog.showProgressDialog(fragmentContext)
        // hide keyboard
        DeviceUtils.hideKeyboard(fragmentContext, fragmentActivity.window.decorView.windowToken)

        // create request
        val request = if (FrameworkUtils.isValidEmail(binding.edtEmailUsername.text.toString())) {
            // set email if valid email
            SigninRequest.Builder()
                .setDeviceType(Constants.DEVICE_TYPE)
                .setEmail(binding.edtEmailUsername.text.toString())
                .setPassword(binding.edtPassword.text.toString())
                .create()
        } else {
            // set username if invalid email
            SigninRequest.Builder()
                .setDeviceType(Constants.DEVICE_TYPE)
                .setUsername(binding.edtEmailUsername.text.toString())
                .setPassword(binding.edtPassword.text.toString())
                .create()
        }

        // make request
        digitaxApiClient.signin(request, object : ResponseCallback<SigninResponse> {
            override fun onSuccess(response: Response.Success<SigninResponse>) {
                // hide progress dialog
                dialog.dismissProgressDialog()
                // sign in user
                goToActivity(MainActivity::class.java, null, true)
            }

            override fun onFailure(failure: Response.Failure<SigninResponse>) {
                // hide progress dialog
                dialog.dismissProgressDialog()
                // show error dialog
                // use extension function for Failure as part of the ResponseUtils
                dialog.createErrorDialog(fragmentContext, failure.getErrorMessage())
            }
        })
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
                ContextCompat.getDrawable(fragmentContext, R.drawable.pill_red_50_rad)
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