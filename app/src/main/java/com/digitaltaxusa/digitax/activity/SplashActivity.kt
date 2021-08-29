package com.digitaltaxusa.digitax.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.constants.Constants
import com.digitaltaxusa.digitax.databinding.ActivitySplashBinding
import com.digitaltaxusa.digitax.fragments.SigninFragment
import com.digitaltaxusa.digitax.room.entity.UserSessionEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import com.digitaltaxusa.digitax.room.viewmodel.UserSessionViewModel
import com.digitaltaxusa.framework.firebase.FirebaseAnalyticsManager
import com.digitaltaxusa.framework.logger.Logger
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils
import kotlinx.android.synthetic.main.activity_splash.view.*
import java.util.concurrent.Executor

private const val REQUEST_CODE_FINGERPRINT_ENROLLMENT = 1000

class SplashActivity : BaseActivity(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: ActivitySplashBinding

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    // biometrics
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    // room database
    private var userSessionViewModel: UserSessionViewModel? = null
    private var userSessionEntity: UserSessionEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        // initialize views and handlers
        initializeViews()
        initializeListeners()
        initializeHandlers()
    }

    /**
     * Method is used to initialize views.
     */
    private fun initializeViews() {
        // log screen event
        // only need to explicitly log SplashActivity because all future navigation
        // is automatically logged in the goToActivity() and addFragment() functions
        firebaseAnalyticsManager.logCurrentScreen(
            SplashActivity::class.java.simpleName
        )
        // initialize biometric executor
        executor = ContextCompat.getMainExecutor(applicationContext)

        // initialize view models
        userSessionViewModel = ViewModelProvider(this).get(UserSessionViewModel::class.java)

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
        setup()
    }

    /**
     * Method is used to initialize listeners and callbacks.
     */
    private fun initializeListeners() {
        // set observer
        // add the given observer to the observers list within the lifespan for Room database
        userSessionViewModel?.entity?.observe(this) { entity ->
            if (entity != null) {
                userSessionEntity = entity

                // check for active user session
                if (!isAccessTokenExpired()) {
                    // sign in user
                    goToActivity(MainActivity::class.java, null, true)
                }
            }
        }
    }

    /**
     * Method is used to initialize click listeners.
     */
    private fun initializeHandlers() {
        binding.tvUsePassword.setOnClickListener(this)
        binding.tvUnlock.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
    }

    /**
     * Method is used to check if access token is expired.
     */
    private fun isAccessTokenExpired() =
        // check to see if our current time is past the token expiration date/time
        System.currentTimeMillis() > userSessionEntity?.accessTokenExpiration ?: 0

    /**
     * Method is used to determine if biometrics is supported and setup UI accordingly.
     */
    private fun setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            checkBiometricAvailability()
        } else {
            // set biometric UI to false
            toggleBiometric(false)
        }
    }

    /**
     * Method is used to update UI based on biometric support.
     *
     * @param isBiometricSupported Boolean True if biometrics is supported, otherwise false.
     */
    private fun toggleBiometric(isBiometricSupported: Boolean) {
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
                dialog.showYesNoDialog(
                    this,
                    getString(R.string.log_out),
                    getString(R.string.dialog_are_you_sure_log_out),
                    null,
                    null,
                    { _, _ ->
                        // clear database and cache
                        // TODO clear database and cache. May have more to do regarding sessions.
                    }
                )
            }
        }
    }

    /**
     * Method is used to check if the user can authenticate with an authenticator
     * that meets the given requirements. This requires at least one of the specified
     * authenticators to be present, enrolled, and available on the device.
     */
    @Suppress("DEPRECATION")
    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun checkBiometricAvailability() {
        val biometricManager = from(this)

        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            // legend
            // BIOMETRIC_SUCCESS = 0
            // BIOMETRIC_STATUS_UNKNOWN = -1
            // BIOMETRIC_ERROR_UNSUPPORTED = -2
            // BIOMETRIC_ERROR_NO_HARDWARE = 12
            // BIOMETRIC_ERROR_HW_UNAVAILABLE = 1
            // BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 15
            // BIOMETRIC_ERROR_NONE_ENROLLED = 11
            BIOMETRIC_SUCCESS -> {
                Logger.d(Constants.TAG, "App can authenticate using biometrics.")
                // set biometric UI to false
                toggleBiometric(true)
                // show biometrics dialog
                showBiometricAuthentication()
            }
            BIOMETRIC_STATUS_UNKNOWN -> {
                Logger.e(Constants.TAG, "Biometric status is unknown.")
                // set biometric UI to false
                toggleBiometric(false)
            }
            BIOMETRIC_ERROR_UNSUPPORTED -> {
                Logger.e(Constants.TAG, "No biometric features available on this device.")
                // set biometric UI to false
                toggleBiometric(false)
            }
            BIOMETRIC_ERROR_NO_HARDWARE -> {
                Logger.e(Constants.TAG, "No biometric features available on this device.")
                // set biometric UI to false
                toggleBiometric(false)
            }
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Logger.e(Constants.TAG, "Biometric features are currently unavailable.")
                // set biometric UI to false
                toggleBiometric(false)
            }
            BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Logger.e(Constants.TAG, "Biometric requires an update.")
                // set biometric UI to false
                toggleBiometric(false)
            }
            BIOMETRIC_ERROR_NONE_ENROLLED -> {
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

                    // track signin
                    val bundle = Bundle()
                    bundle.putString(
                        FirebaseAnalyticsManager.Property.KEY_LOGIN_TYPE,
                        Enums.LoginType.BIOMETRIC.toString()
                    )
                    firebaseAnalyticsManager.logEvent(
                        FirebaseAnalyticsManager.Event.SIGN_IN,
                        bundle
                    )

                    // update database
                    userSessionEntity?.deviceId = FrameworkUtils.getDeviceId(this@SplashActivity)
                    userSessionEntity?.loginType = Enums.LoginType.BIOMETRIC.toString()
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
