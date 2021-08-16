package com.digitaltaxusa.digitax.activity

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.constants.Constants
import com.digitaltaxusa.digitax.databinding.ActivityMainContainerBinding
import com.digitaltaxusa.digitax.fragments.BaseFragment
import com.digitaltaxusa.digitax.fragments.LocationServicesFragment
import com.digitaltaxusa.digitax.fragments.WebViewFragment
import com.digitaltaxusa.digitax.network.NetworkReceiver
import com.digitaltaxusa.digitax.network.listeners.NetworkStatusObserver
import com.digitaltaxusa.digitax.room.entity.DrivingEntity
import com.digitaltaxusa.digitax.room.viewmodel.DrivingViewModel
import com.digitaltaxusa.framework.logger.Logger
import com.digitaltaxusa.framework.map.listeners.AddressListener
import com.digitaltaxusa.framework.map.listeners.GoogleServicesApiInterface
import com.digitaltaxusa.framework.map.listeners.OnLocationPermissionListener
import com.digitaltaxusa.framework.map.model.Address
import com.digitaltaxusa.framework.map.provider.GoogleServicesApiProvider
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.DistanceUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView

// threshold for valid traveled distance (in meters)
private const val MINIMUM_TRAVELED_DISTANCE = 10f
// threshold to report GPS signals to Firebase
private const val CONFIDENCE_QUEUE_MAX_SIZE = 5
private const val LOCATION_REQUEST_INTERVAL = 30000L // milliseconds
private const val LOCATION_REQUEST_FASTEST_INTERVAL = 15000L // milliseconds
private const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener, LocationListener,
    NavigationView.OnNavigationItemSelectedListener, NetworkStatusObserver {

    // view binding and layout widgets
    // this property is only valid between onCreateView and onDestroyView
    private lateinit var binding: ActivityMainContainerBinding

    // views and drawers
    private var drawerToolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private lateinit var drawerLayoutParent: View
    private lateinit var locationServicesFragment: LocationServicesFragment

    // location tracking
    private val googleServiceApiClient: GoogleServicesApiInterface = GoogleServicesApiProvider.getInstance()
    private val alConfidenceQueue: ArrayList<Float> = arrayListOf()
    private var currentLocation: Location? = null
    private var currentLatLng: LatLng? = null
    private var distance: Float = 0f
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    // network
    private lateinit var networkReceiver: NetworkReceiver

    // room database
    private var drivingViewModel: DrivingViewModel? = null
    private var drivingEntities: List<DrivingEntity> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainContainerBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        // instantiate views and listeners
        initializeViews()
        initializeHandlers()
        initializeListeners()
        initializeDrawer()
        // current location
        getLastKnownLocation()

        // TODO methods below are test methods. Delete them once finished.
        // uncomment to test mileage tracking
//        testMileageTracking()
        testGoogleServices()
    }

    /**
     * Method is used to initialize views.
     */
    private fun initializeViews() {
        // initialize views
        networkReceiver = NetworkReceiver()
        locationServicesFragment = LocationServicesFragment()

        // initialize view models
        drivingViewModel = ViewModelProvider(this).get(DrivingViewModel::class.java)

        // request location updates
        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_REQUEST_INTERVAL
            fastestInterval = LOCATION_REQUEST_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        // create a new instance of FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // set adapter for arrays
        val months = resources.getStringArray(R.array.array_months)
        val adapterMonths = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            months
        )
        binding.layoutActivityMain.layoutToolbar.spnrMonths.adapter = adapterMonths

        // drawer
        drawerToolbar = binding.layoutActivityMain.layoutToolbar.toolbar
        drawerLayout = binding.drawerLayout
        drawerLayoutParent = binding.layoutDrawer.rlDrawerParent

        // set labels (sub-toolbar)
        // the values for these labels require API call to retrieve
        binding.layoutActivityMain.milesDriven.tvLabel.text = getString(R.string.miles_driven)
        binding.layoutActivityMain.drives.tvLabel.text = getString(R.string.drives)
        binding.layoutActivityMain.events.tvLabel.text = getString(R.string.events)
        binding.layoutActivityMain.expenses.tvLabel.text = getString(R.string.expenses)
        // set defaults
        binding.layoutActivityMain.milesDriven.tvValue.text = getString(R.string.default_value)
        binding.layoutActivityMain.drives.tvValue.text = getString(R.string.default_value)
        binding.layoutActivityMain.events.tvValue.text = getString(R.string.default_value)
        binding.layoutActivityMain.expenses.tvValue.text = getString(R.string.default_dollar_value)

        // check if location permissions and services are granted/enabled
        if (!checkPermissions()) {
            // add fragment
            addFragment(locationServicesFragment)
        }
    }

    /**
     * Method is used to initialize click listeners.
     */
    private fun initializeHandlers() {
        // spinner
        binding.layoutActivityMain.layoutToolbar.spnrMonths.onItemSelectedListener = this
    }

    /**
     * Method is used to initialize listeners and callbacks.
     */
    private fun initializeListeners() {
        // drawer listener
        drawerLayout?.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                // no-opt
            }

            override fun onDrawerClosed(drawerView: View) {
                // no-opt
            }

            override fun onDrawerStateChanged(newState: Int) {
                // no-opt
            }
        })

        // location permission listener
        locationServicesFragment.onLocationPermissionListener(object :
            OnLocationPermissionListener {
            override fun onLocationPermission(isEnabled: Boolean) {
                if (isEnabled) {
                    if (currentLatLng == null) {
                        // current location
                        getLastKnownLocation()
                    }
                } else {
                    if (currentLatLng == null) {
                        // default location
                        currentLatLng = LatLng(0.0, 0.0)
                    }
                }
            }
        })

        // location callback listener
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // process location change
                processOnLocationChanged(locationResult.lastLocation)
            }
        }

        // set observer
        // add the given observer to the observers list within the lifespan for Room database
        drivingViewModel?.entities?.observe(this) { entities ->
            if (entities.isNotEmpty()) {
                drivingEntities = entities
            }
        }
    }

    /**
     * Method is used to initialize drawer.
     */
    private fun initializeDrawer() {
        // set a Toolbar to act as the ActionBar for this Activity window
        setSupportActionBar(drawerToolbar)
        // this class provides a handy way to tie together the functionality of
        // DrawerLayout and the framework ActionBar to implement the recommended
        // design for navigation drawers.
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            drawerToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                // TODO add any logic for when drawer is closed e.g. start services
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                // TODO add any logic for when drawer is open e.g. stop services
            }
        }
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        // navigation drawer
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        // set a listener that will be notified when a menu item is selected
        navigationView.setNavigationItemSelectedListener(this)
        // set the tint which is applied to our menu items' icons
        navigationView.itemIconTintList = null
    }

    /**
     * Method is used to determine whether location permissions have been granted. If no
     * location permissions are granted, launch [LocationServicesFragment].
     *
     * @return Boolean True if location permission and services is enabled, otherwise false.
     */
    private fun checkPermissions(): Boolean {
        val isAppPermissionsEnabled = FrameworkUtils.checkAppPermissions(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val isLocationServicesEnabled = FrameworkUtils.isLocationServiceEnabled(
            this
        )
        return isAppPermissionsEnabled && isLocationServicesEnabled
    }

    /**
     * Method is used to process onLocationChanged behaviors.
     *
     * <p>Method also tracks GPS signal strength [trackGpsSignalStrength] on Firebase
     * using an updated location object.</p>
     *
     * @param location Location A data class representing a geographic location.
     */
    private fun processOnLocationChanged(location: Location) {
        // track total miles test code
        val tempDistance: Float = currentLocation?.distanceTo(location) ?: 0f

        // anything less than 10 meters difference is too small. There won't be
        // any updates during this scenario
        if (currentLocation == null || tempDistance > MINIMUM_TRAVELED_DISTANCE) {
            distance += tempDistance
            // convert meters to miles
            binding.layoutActivityMain.milesDriven.tvValue.text = DistanceUtils.meterToMile(
                distance
            ).toString()

            // update location
            currentLocation = location
            currentLatLng = LatLng(location.latitude, location.longitude)

            // track gps strength
            trackGpsSignalStrength(location)
        }
    }

    /**
     * Method is used to track GPS accuracy.
     *
     * <p>A GPS strength profile is generated on Firebase analytics. The profile will
     * consist of poor GPS signals that are timestamped and geographically marked. Data
     * on areas that produce the poorest location accuracy will be able to turn into
     * useful reports.</p>
     *
     * @param location Location A data class representing a geographic location.
     */
    private fun trackGpsSignalStrength(location: Location) {
        if (location.accuracy <= 0f) {
            // reset confidence queue
            alConfidenceQueue.clear()
        } else if (location.accuracy >= 50f) {
            // poor signal
            alConfidenceQueue.add(location.accuracy)
            if (alConfidenceQueue.size > CONFIDENCE_QUEUE_MAX_SIZE) {
                // report GPS signals to Firebase
                // TODO log to Firebase
                // reset confidence queue
                alConfidenceQueue.clear()
            }
        } else {
            // decent signal
            if (alConfidenceQueue.size > 0) {
                alConfidenceQueue.clear()
            }
        }
    }

    /**
     * Returns the best most recent location currently available.
     *
     * <p>If a location is not available, which should happen very rarely, null will
     * be returned. The best accuracy available while respecting the location permissions
     * will be returned. This method provides a simplified way to get location. It is
     * particularly well suited for applications that do not require an accurate location
     * and that do not want to maintain extra logic for location updates.</p>
     */
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // set current location
            currentLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
            // start location updates
            startLocationUpdates()
        }
    }

    /**
     * Method is used to request location updates.
     *
     * Ref-https://developer.android.com/training/location/request-updates
     *
     * <p>Appropriate use of location information can be beneficial to users of your app.
     * For example, if your app helps the user find their way while walking or driving, or
     * if your app tracks the location of assets, it needs to get the location of the device
     * at regular intervals. As well as the geographical location (latitude and longitude),
     * you may want to give the user further information such as the bearing (horizontal
     * direction of travel), altitude, or velocity of the device. This information,
     * and more, is available in the Location object that your app can retrieve from
     * the fused location provider.</p>
     */
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // request location updates
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * Method is used to open and close the drawer.
     *
     * @param isOpen True if drawer is open, otherwise false.
     */
    fun setDrawerState(isOpen: Boolean) {
        if (isOpen) {
            drawerLayout?.openDrawer(drawerLayoutParent)
        } else {
            drawerLayout?.closeDrawer(drawerLayoutParent)
        }
    }

    /**
     * Method is used to unlock or lock drawer interaction.
     *
     * @param isUnlocked Boolean True to unlock drawer interaction, otherwise false.
     */
    fun setDrawerUnlockMode(isUnlocked: Boolean) {
        if (isUnlocked) {
            // only unlock drawer interaction if it is disabled
            if (drawerLayout?.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED) {
                drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        } else {
            // only allow disabling of drawer interaction if the drawer is closed
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // TODO("Not yet implemented")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        val args = Bundle()

        when (item.itemId) {
//            R.id.nav_guide -> fragment = GuideFragment()
//            R.id.nav_settings -> fragment = SettingsFragment()
//            R.id.nav_history -> fragment = HistoryFragment()
//            R.id.nav_share -> fragment = ShareFragment()
//            R.id.nav_about -> fragment = AboutFragment()
            R.id.nav_privacy -> {
                // hide navigation bar
                drawerToolbar?.visibility = View.GONE
                // set fragment
                fragment = WebViewFragment()
                args.putString(Constants.KEY_WEB_VIEW_HEADER, item.toString())
                args.putString(Constants.KEY_WEB_VIEW_URL, Constants.PRIVACY_URL)
                fragment.arguments = args
                fragment.setOnRemoveListener(object : BaseFragment.OnRemoveFragment {
                    override fun onRemove() {
                        // show navigation bar
                        drawerToolbar?.visibility = View.VISIBLE
                    }
                })
            }
            R.id.nav_terms -> {
                // hide navigation bar
                drawerToolbar?.visibility = View.GONE
                // set fragment
                fragment = WebViewFragment()
                args.putString(Constants.KEY_WEB_VIEW_HEADER, item.toString())
                args.putString(Constants.KEY_WEB_VIEW_URL, Constants.TERMS_URL)
                fragment.arguments = args
                fragment.setOnRemoveListener(object : BaseFragment.OnRemoveFragment {
                    override fun onRemove() {
                        // show navigation bar
                        drawerToolbar?.visibility = View.VISIBLE
                    }
                })
            }
            else -> {
                // no-op
            }
        }
        // add fragment
        if (fragment != null) {
            addFragment(fragment)
        }
        // close drawer after selection
        drawerLayout?.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onLocationChanged(location: Location) {
        Log.e("DATMUG", "onLocationChanged")
        currentLocation = location
    }

    override fun onResume() {
        super.onResume()
        // only register receiver if it has not already been registered
        if (!networkReceiver.contains(this)) {
            // register network receiver
            networkReceiver.addObserver(this)
            registerReceiver(
                networkReceiver,
                IntentFilter(CONNECTIVITY_ACTION)
            )
            // print observer list
            networkReceiver.printObserverList()
        }
    }

    override fun onPause() {
        // unregister network receiver (observer)
        if (networkReceiver.getObserverSize() > 0 && networkReceiver.contains(this)) {
            try {
                // unregister network receiver
                unregisterReceiver(networkReceiver)
            } catch (e: IllegalStateException) {
                Logger.e(Constants.TAG, e.message.orEmpty(), e)
                e.printStackTrace()
            }
            networkReceiver.removeObserver(this)
        }
        super.onPause()
    }

    override fun onBackPressed() {
        // handle device back button various states
        if (drawerLayout?.isDrawerOpen(drawerLayoutParent) == true) {
            // if drawer is open, close it
            setDrawerState(false)
        } else {
            super.onBackPressed()
        }
    }

    override fun notifyConnectionChange(isConnected: Boolean) {
        if (isConnected) {
            // app is connected to network
            dialog.dismissNoNetworkDialog()
        } else {
            // app is not connected to network
            dialog.showNoNetworkDialog(
                this,
                resources.getString(R.string.dialog_title_check_network),
                resources.getString(R.string.dialog_check_network)
            )
        }
    }

    /**
     * TODO - TESTING PURPOSES ONLY: Delete function
     */
    private fun testMileageTracking() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // home
            // 28111 Robin Ave, Santa Clarita CA 91350
            Log.v("DATMUG", "home (starting)")
            val location = Location("provider")
            location.latitude = 34.4520644
            location.longitude = -118.4977783
            processOnLocationChanged(location)
        }, 5000)

        handler.postDelayed({
            // home -> ucla = 6.5mi (5.12mi)
            // ucla hospital
            // 27235 Tourney Rd, Santa Clarita, CA 91355
            Log.v("DATMUG", "ucla hospital")
            val location = Location("provider")
            location.latitude = 34.417670
            location.longitude = -118.577320
            processOnLocationChanged(location)
        }, 15000)

        handler.postDelayed({
            // ucla -> magic mountain = 1.2 mi (0.6mi)
            // running total = 7.7mi
            // magic mountain
            // 26101 Magic Mountain Pkwy, Valencia, CA 91355
            Log.v("DATMUG", "magic mountain")
            val location = Location("provider")
            location.latitude = 34.420879
            location.longitude = -118.587097
            processOnLocationChanged(location)
        }, 20000)

        handler.postDelayed({
            // magic mountain -> csun = 19.2mi (13.19mi)
            // running total = 26.9mi
            // csun
            // 18111 Nordhoff St, Northridge, CA 91330
            Log.v("DATMUG", "csun")
            val location = Location("provider")
            location.latitude = 34.235512
            location.longitude = -118.531723
            processOnLocationChanged(location)
        }, 25000)

        handler.postDelayed({
            // csun -> ipic = 28.2mi (22.62mi)
            // running total = 55.1mi
            // pasadena - ipic
            // 42 Miller Alley, Pasadena, CA 91103
            Log.v("DATMUG", "pasadena - ipic")
            val location = Location("provider")
            location.latitude = 34.146430
            location.longitude = -118.150940
            processOnLocationChanged(location)
        }, 30000)

        handler.postDelayed({
            // ipic -> sea world = 125.8mi (109.34mi)
            // running total = 180.9mi
            // sea world
            // 500 Sea World Dr., San Diego, CA 92109
            Log.v("DATMUG", "sea world")
            val location = Location("provider")
            location.latitude = 32.765300
            location.longitude = -117.224780
            processOnLocationChanged(location)
        }, 35000)
    }

    /**
     * TODO - TESTING PURPOSES ONLY: Delete function
     */
    private fun testGoogleServices() {
        val origin = LatLng(34.4520644, -118.4977783)
        val destination = LatLng(32.765300, -117.224780)


        googleServiceApiClient.getAddress(origin, object : AddressListener {
            override fun onAddressResponse(address: Address?) {
                TODO("Not yet implemented")
            }

            override fun onAddressError() {
                TODO("Not yet implemented")
            }

            override fun onZeroResults() {
                TODO("Not yet implemented")
            }

        })
    }
}