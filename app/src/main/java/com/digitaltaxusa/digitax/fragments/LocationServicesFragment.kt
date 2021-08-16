package com.digitaltaxusa.digitax.fragments

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.constants.Constants.TAG
import com.digitaltaxusa.digitax.databinding.FragmentLocationPermissionsBinding
import com.digitaltaxusa.framework.logger.Logger
import com.digitaltaxusa.framework.map.listeners.OnLocationPermissionListener
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils

class LocationServicesFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: FragmentLocationPermissionsBinding

    // listener
    private var locationPermissionListener: OnLocationPermissionListener? = null

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    /**
     * Method is used to set callback for when location permissions and GPS is enabled.
     *
     * @param listener Callback for when location permissions and GPS is enabled.
     */
    fun onLocationPermissionListener(listener: OnLocationPermissionListener) {
        locationPermissionListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationPermissionsBinding.inflate(inflater, container, false)

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
        // disable drawer interaction
//        (activity as MainActivity).setDrawerUnlockMode(false)
    }

    /**
     * Method is used to initialize click listeners.
     */
    private fun initializeHandlers() {
        binding.tvEnableLocationPermissions.setOnClickListener(this)
    }

    /**
     * Method is used to initialize listeners and callbacks.
     */
    private fun initializeListeners() {

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
            R.id.tv_enable_location_permissions -> {
                // handle permissions
                handlePermissions()
            }
        }
    }

    /**
     * Determine whether location permissions have been granted. If no location
     * permissions are granted, the user should be directed to enable location
     * permissions to use the app. Otherwise, they will fall into an alternative
     * flow.
     */
    private fun handlePermissions() {
        // location permissions enabled flag
        val isAppPermissionsEnabled = FrameworkUtils.checkAppPermissions(
            requireContext(),
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
        // location services enabled flag
        val isLocationServicesEnabled = FrameworkUtils.isLocationServiceEnabled(
            requireContext()
        )

        if (!isAppPermissionsEnabled) {
            val requestMultiplePermissions = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                permissions.entries.forEach {
                    Logger.d(TAG, "${it.key} = ${it.value}")
                }
                if (permissions[ACCESS_FINE_LOCATION] == true && permissions[ACCESS_COARSE_LOCATION] == true) {
                    // permissions granted
                    permissionsGranted()
                }
            }

            // request location permission if permission is not enabled
            requestMultiplePermissions.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
            )
        } else if (!isLocationServicesEnabled) {
            // request location services if location services is not enabled
            // show location services dialog
            showLocationServiceDisabledDialog()
        } else {
            // permissions granted
            permissionsGranted()
        }
    }

    /**
     * Method is called once all necessary permissions for location services is granted.
     */
    private fun permissionsGranted() {
        // location permission and location services are both enabled
        // set listener
        locationPermissionListener?.onLocationPermission(true)
        // remove fragment
        remove()
    }

    /**
     * Method is used to show dialog that location services is not enabled.
     */
    private fun showLocationServiceDisabledDialog() {
        dialog.showYesNoDialog(requireContext(), resources.getString(R.string.settings),
            getString(
                R.string.enable_location_services_message,
                resources.getString(R.string.app_name)
            ),
            resources.getString(R.string.ok),
            resources.getString(R.string.cancel), { _, _ ->
                // dismiss dialog
                dialog.dismissDialog()
                // show settings to allow configuration of current location sources
                val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                startActivity(Intent(action))
            }, { _, _ ->
                // dismiss dialog
                dialog.dismissDialog()
            })
    }
}