package com.digitaltaxusa.digitax.activity

import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.constants.Constants
import com.digitaltaxusa.digitax.databinding.ActivitySplashBinding
import com.digitaltaxusa.digitax.fragments.SigninFragment
import com.digitaltaxusa.framework.logger.Logger
import com.digitaltaxusa.framework.utils.FrameworkUtils
import kotlinx.android.synthetic.main.activity_splash.view.*
import java.util.concurrent.Executor

class SplashActivity : BaseActivity(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: ActivitySplashBinding

    // biometrics
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        // initialize views and handlers
        initializeViews()
        initializeHandlers()
    }

    /**
     * Method is used to initialize views
     */
    private fun initializeViews() {
        // log screen event
        firebaseAnalyticsManager.logCurrentScreen(
            this,
            SplashActivity::class.java.simpleName
        )
        // initialize biometric executor
        executor = ContextCompat.getMainExecutor(applicationContext)

        // terms and privacy
        FrameworkUtils.linkify(
            binding.fragContainer.tv_terms, resources.getString(R.string.terms),
            Constants.TERMS_URL
        )
        FrameworkUtils.linkify(
            binding.fragContainer.tv_privacy_policy, resources.getString(R.string.privacy_policy),
            Constants.PRIVACY_URL
        )

        // show biometrics dialog
        showBiometricAuthentication()
    }

    /**
     * Method is used to initialize click listeners
     */
    private fun initializeHandlers() {
        binding.llEmailAuthWrapper.setOnClickListener(this)
        binding.llBiometricAuthWrapper.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (!FrameworkUtils.isViewClickable) {
            return
        }
        when (v.id) {
            R.id.ll_email_auth_wrapper -> {
                // add fragment
                addFragment(SigninFragment())
            }
            R.id.ll_biometric_auth_wrapper -> {
                // show biometrics dialog
                showBiometricAuthentication()
            }
        }
    }

    /**
     * Method is used to display the biometric authentication dialog. One method of
     * protecting sensitive information or premium content within your app is to request
     * biometric authentication, such as using face recognition or fingerprint recognition.
     *
     * Source: https://developer.android.com/training/sign-in/biometric-auth#display-login-prompt
     *
     * <p>Note: The Biometric library expands upon the functionality of the deprecated
     * FingerprintManager API.
     *
     * Source: https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager</p>
     *
     */
    private fun showBiometricAuthentication() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Logger.e(Constants.TAG, "Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Logger.e(Constants.TAG, "Authentication succeeded!")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Logger.e(Constants.TAG, "Authentication failed")
                }
            })

        // creates a BiometricPrompt
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.dialog_title_biometrics))
            .setSubtitle(resources.getString(R.string.dialog_subtitle_biometrics))
            .setNegativeButtonText(resources.getString(R.string.dialog_negative_button_biometrics))
            .build()

        // shows the biometric prompt. The prompt survives lifecycle changes by default.
        // To cancel the authentication, use cancelAuthentication().
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onResume() {
        super.onResume()
        // print info
        FrameworkUtils.printInfo(this)
    }
}
