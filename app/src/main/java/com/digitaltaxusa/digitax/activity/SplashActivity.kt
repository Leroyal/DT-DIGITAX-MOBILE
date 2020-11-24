package com.digitaltaxusa.digitax.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
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


private const val REQUEST_CODE_FINGERPRINT_ENROLLMENT = 1000

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

        // setup UI components
        setupUI()
    }

    /**
     * Method is used to initialize click listeners
     */
    private fun initializeHandlers() {
        binding.tvUsePassword.setOnClickListener(this)
        binding.tvUnlock.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
    }

    /**
     * Method is used to determine if biometrics is supported and setup UI accordingly.
     */
    private fun setupUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            checkBiometricAvailability()
        } else {
            // set biometric UI to false
            toggleBiometricUI(false)
        }
    }

    /**
     * Method is used to update UI based on biometric support
     *
     * @param isBiometricSupported Boolean True if biometrics is supported, otherwise false.
     */
    private fun toggleBiometricUI(isBiometricSupported: Boolean) {
        if (isBiometricSupported) {
            // show biometric button
            binding.tvUnlock.visibility = View.VISIBLE
            binding.tvLogout.background = null
        } else {
            // if device is not high enough API, hide biometric button
            binding.tvUnlock.visibility = View.GONE
            binding.tvLogout.background = ContextCompat.getDrawable(
                this,
                R.drawable.pill_red_50_rad
            )
        }
    }

    override fun onClick(v: View) {
        if (!FrameworkUtils.isViewClickable) {
            return
        }
        when (v.id) {
            R.id.tv_use_password -> {
                // add fragment
                addFragment(SigninFragment())
            }
            R.id.tv_unlock -> {
                // show biometrics dialog
                showBiometricAuthentication()
            }
            R.id.tv_logout -> {
                // clear database and cache
                // TODO need to clear database and cache
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            // legend
            // BIOMETRIC_SUCCESS = 0
            // BIOMETRIC_ERROR_NO_HARDWARE = 12
            // BIOMETRIC_ERROR_HW_UNAVAILABLE = 1
            // BIOMETRIC_ERROR_NONE_ENROLLED = 11
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Logger.d(Constants.TAG, "App can authenticate using biometrics.")
                // set biometric UI to false
                toggleBiometricUI(true)
                // show biometrics dialog
                showBiometricAuthentication()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Logger.e(Constants.TAG, "No biometric features available on this device.")
                // set biometric UI to false
                toggleBiometricUI(false)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Logger.e(Constants.TAG, "Biometric features are currently unavailable.")
                // set biometric UI to false
                toggleBiometricUI(false)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts
                val intent = Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                startActivityForResult(intent, REQUEST_CODE_FINGERPRINT_ENROLLMENT)
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
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // add fragment
                        addFragment(SigninFragment())
                    }
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
