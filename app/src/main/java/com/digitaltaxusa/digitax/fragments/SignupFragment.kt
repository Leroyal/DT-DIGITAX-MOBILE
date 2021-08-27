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
import androidx.lifecycle.ViewModelProvider
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.activity.BaseActivity
import com.digitaltaxusa.digitax.activity.MainActivity
import com.digitaltaxusa.digitax.api.client.ApiInterface
import com.digitaltaxusa.digitax.api.provider.DigitaxApiProvider
import com.digitaltaxusa.digitax.api.requests.SignupRequest
import com.digitaltaxusa.digitax.api.response.SignupResponse
import com.digitaltaxusa.digitax.constants.Constants
import com.digitaltaxusa.digitax.databinding.FragmentSignupBinding
import com.digitaltaxusa.digitax.room.entity.UserSessionEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import com.digitaltaxusa.digitax.room.viewmodel.UserSessionViewModel
import com.digitaltaxusa.framework.device.DeviceUtils
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.http.response.Response
import com.digitaltaxusa.framework.http.response.ResponseCallback
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils
import com.digitaltaxusa.framework.utils.getErrorMessage

class SignupFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: FragmentSignupBinding

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    // api client and configuration
    private val digitaxApiClient: ApiInterface = DigitaxApiProvider.getInstance()

    // room database
    private var userSessionViewModel: UserSessionViewModel? = null
    private var userSessionEntity: UserSessionEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        // instantiate views and listeners
        initializeViews()
        initializeHandlers()
        initializeListeners()
        // return root
        return binding.root
    }

    /**
     * Method is used to initialize views.
     */
    private fun initializeViews() {
        // set header
        binding.header.tvHeader.text = resources.getString(R.string.sign_up)
        // request focus
        binding.edtUsername.requestFocus()

        // initialize view models
        userSessionViewModel = ViewModelProvider(this).get(UserSessionViewModel::class.java)

        // set CTA state
        setCtaEnabled(false)
    }

    /**
     * Method is used to initialize click listeners.
     */
    private fun initializeHandlers() {
        binding.header.ivBack.setOnClickListener(this)
        binding.tvSignupCta.setOnClickListener(this)
        binding.tvShowPassword.setOnClickListener(this)
        binding.tvShowPasswordConfirm.setOnClickListener(this)
    }

    /**
     * Method is used to initialize listeners and callbacks.
     */
    private fun initializeListeners() {
        // username editTextChangeListener
        binding.edtUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //do nothing
            }

            override fun afterTextChanged(s: Editable) {
                // set CTA state
                setCtaEnabled(
                    s.toString().isNotEmpty() &&
                            FrameworkUtils.isValidEmail(binding.edtEmail.text.toString()) &&
                            FrameworkUtils.isValidPassword(binding.edtPasswordConfirm.text.toString()) &&
                            FrameworkUtils.isValidPassword(binding.edtPassword.text.toString())
                )
            }
        })
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
                    binding.edtUsername.text.toString().isNotEmpty() &&
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
                    binding.edtUsername.text.toString().isNotEmpty() &&
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
                    binding.edtUsername.text.toString().isNotEmpty() &&
                            FrameworkUtils.isValidEmail(binding.edtEmail.text.toString()) &&
                            FrameworkUtils.isValidPassword(s.toString()) &&
                            FrameworkUtils.isValidPassword(binding.edtPassword.text.toString())
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
                signup()
            }
        }
    }

    /**
     * Method is used to make /signup request.
     */
    private fun signup() {
        // show progress dialog
        dialog.showProgressDialog(fragmentContext)
        // hide keyboard
        DeviceUtils.hideKeyboard(fragmentContext, fragmentActivity.window.decorView.windowToken)

        // create request
        val request = SignupRequest.Builder()
            .setDeviceType(Constants.DEVICE_TYPE)
            .setUsername(binding.edtUsername.text.toString())
            .setEmail(binding.edtEmail.text.toString())
            .setPassword(binding.edtPassword.text.toString())
            .create()

        // make request
        digitaxApiClient.signup(request, object : ResponseCallback<SignupResponse> {
            override fun onSuccess(response: Response.Success<SignupResponse>) {
                // hide progress dialog
                dialog.dismissProgressDialog()

                // track signup
                val bundle = Bundle()
                bundle.putString(FirebaseAnalyticsManager.Params.LOGIN_TYPE, Enums.LoginType.EMAIL.toString())
                firebaseAnalyticsManager.logEvent(FirebaseAnalyticsManager.Event.SIGN_UP, bundle)

                // update database
                userSessionEntity?.deviceId = FrameworkUtils.getDeviceId(requireContext())
                userSessionEntity?.username = binding.edtUsername.text.toString()
                userSessionEntity?.emailAddress = binding.edtEmail.text.toString()
                userSessionEntity?.loginType = Enums.LoginType.EMAIL.toString()
                // TODO expiration & user information needs to come from backend.
                // TODO Should follow database insert with update for connected information
                // TODO based on deviceId. Related to Jira ticket DIG-73
                // TODO Ref-https://digitaltaxusa.atlassian.net/browse/DIG-73
                userSessionEntity?.accessTokenExpiration = FrameworkUtils.addDaysToCurrentDate(
                    7
                ).timeInMillis
                userSessionEntity?.let { userSessionViewModel?.insert(it) }

                // sign in user
                goToActivity(MainActivity::class.java, null, true)
            }

            override fun onFailure(failure: Response.Failure<SignupResponse>) {
                // hide progress dialog
                dialog.dismissProgressDialog()
                // show error dialog
                // use extension function for Failure as part of the ResponseUtils
                dialog.createErrorDialog(fragmentContext, failure.getErrorMessage())
            }
        })
    }

    /**
     * Method is used to enable / disable the Call To Action button.
     *
     * @param isEnabled True if the CTA button should be enabled, otherwise false.
     */
    private fun setCtaEnabled(isEnabled: Boolean) {
        binding.tvSignupCta.isEnabled = isEnabled
        if (isEnabled) {
            binding.tvSignupCta.setTextColor(ContextCompat.getColor(fragmentContext, R.color.white))
            binding.tvSignupCta.background =
                ContextCompat.getDrawable(fragmentContext, R.drawable.pill_red_50_rad)
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