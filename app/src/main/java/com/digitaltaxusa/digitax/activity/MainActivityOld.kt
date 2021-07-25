//package com.digitaltaxusa.digitax.activity
//
//import android.Manifest
//import android.content.IntentFilter
//import android.content.pm.PackageManager
//import android.content.res.Resources.NotFoundException
//import android.location.Location
//import android.os.Bundle
//import android.os.Looper
//import android.view.GestureDetector
//import android.view.MenuItem
//import android.view.ScaleGestureDetector
//import android.view.View
//import android.widget.ImageView
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.widget.Toolbar
//import androidx.core.app.ActivityCompat
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import com.digitaltaxusa.digitax.R
//import com.digitaltaxusa.digitax.constants.Constants
//import com.digitaltaxusa.digitax.databinding.ActivityMainBinding
//import com.digitaltaxusa.digitax.fragments.BaseFragment
//import com.digitaltaxusa.digitax.fragments.LocationServicesFragment
//import com.digitaltaxusa.digitax.fragments.WebViewFragment
//import com.digitaltaxusa.digitax.fragments.map.MapFragment
//import com.digitaltaxusa.digitax.fragments.map.listeners.OnLocationPermissionListener
//import com.digitaltaxusa.digitax.fragments.map.listeners.OnMapTouchListener
//import com.digitaltaxusa.digitax.fragments.map.listeners.gestures.GestureListener
//import com.digitaltaxusa.digitax.fragments.map.listeners.gestures.MapTouchListener
//import com.digitaltaxusa.digitax.fragments.map.listeners.gestures.ScaleGestureListener
//import com.digitaltaxusa.digitax.network.NetworkReceiver
//import com.digitaltaxusa.digitax.network.listeners.NetworkStatusObserver
//import com.digitaltaxusa.framework.logger.Logger
//import com.digitaltaxusa.framework.utils.DialogUtils
//import com.digitaltaxusa.framework.utils.FrameworkUtils
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.UiSettings
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MapStyleOptions
//import com.google.android.material.navigation.NavigationView
//import kotlinx.android.synthetic.main.activity_map.view.*
//import kotlinx.android.synthetic.main.drawer.view.*
//
//private const val DEFAULT_ZOOM_LEVEL = 17f
//private const val LOCATION_REQUEST_INTERVAL = 30000L // milliseconds
//private const val LOCATION_REQUEST_FASTEST_INTERVAL = 15000L // milliseconds
//private const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
//
//class MainActivityOld : BaseActivity(), View.OnClickListener, OnMapReadyCallback, LocationListener,
//    NavigationView.OnNavigationItemSelectedListener, NetworkStatusObserver {
//
//    // view binding and layout widgets
//    // this property is only valid between onCreateView and onDestroyView
//    private lateinit var binding: ActivityMainBinding
//
//    // views and drawers
//    private lateinit var drawerLayoutParent: View
//    private lateinit var locationServicesFragment: LocationServicesFragment
//    private var drawerToolbar: Toolbar? = null
//    private var drawerLayout: DrawerLayout? = null
//    private var ivRecenterMap: ImageView? = null
//
//    // dialog
//    private var dialog: DialogUtils = DialogUtils()
//
//    // google client
//    private var currentLocation: Location? = null
//    private var currentLatLng: LatLng? = null
//    private var googleMap: GoogleMap? = null
//    private var mapFragment: MapFragment? = null
//    private var mapSettings: UiSettings? = null
//
//    private var isRecenterMap: Boolean = false
//    private var isCameraChanging: Boolean = false
//    private val alConfidenceQueue: ArrayList<Float> = arrayListOf()
//
//    private lateinit var googleMap: GoogleMap
//    private lateinit var locationCallback: LocationCallback
//    private lateinit var locationRequest: LocationRequest
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    // map listeners
//    private var gestureDetector: GestureDetector? = null
//    private var scaleDetector: ScaleGestureDetector? = null
//
//    // network
//    private lateinit var networkReceiver: NetworkReceiver
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater).apply {
//            setContentView(root)
//        }
//
//        // instantiate views and listeners
//        initializeViews()
//        initializeHandlers()
//        initializeListeners()
//        initializeDrawer()
//    }
//
//    /**
//     * Method is used to initialize views.
//     */
//    private fun initializeViews() {
//        // initialize map fragment
//        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
//        // request location updates
//        locationRequest = LocationRequest.create().apply {
//            interval = LOCATION_REQUEST_INTERVAL
//            fastestInterval = LOCATION_REQUEST_FASTEST_INTERVAL
//            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//        }
//        // create a new instance of FusedLocationProviderClient
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        // sets a callback object which will be triggered when the GoogleMap instance
//        // is ready to be used
//        mapFragment?.getMapAsync(this)
//
//        // initialize views
//        networkReceiver = NetworkReceiver()
//        locationServicesFragment = LocationServicesFragment()
//        drawerToolbar = binding.drawerLayout.toolbar
//        drawerLayout = binding.drawerLayout
//        drawerLayoutParent = binding.drawerLayout.rl_drawer_parent
//        ivRecenterMap = binding.drawerLayout.iv_recenter_map
//
//        // check if location permissions and services are granted/enabled
//        if (!checkPermissions()) {
//            // add fragment
//            addFragment(locationServicesFragment)
//        }
//    }
//
//    /**
//     * Method is used to initialize click listeners.
//     */
//    private fun initializeHandlers() {
//        ivRecenterMap?.setOnClickListener(this)
//    }
//
//    /**
//     * Method is used to initialize listeners and callbacks.
//     */
//    private fun initializeListeners() {
//        // drawer listener
//        drawerLayout?.addDrawerListener(object : DrawerLayout.DrawerListener {
//            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//
//            }
//
//            override fun onDrawerOpened(drawerView: View) {
//                // no-opt
//            }
//
//            override fun onDrawerClosed(drawerView: View) {
//                // no-opt
//            }
//
//            override fun onDrawerStateChanged(newState: Int) {
//                // no-opt
//            }
//        })
//
//        // location permission listener
//        locationServicesFragment.onLocationPermissionListener(object :
//            OnLocationPermissionListener {
//            override fun onLocationPermission(isEnabled: Boolean) {
//                if (isEnabled) {
//                    if (currentLatLng == null) {
//                        // current location
//                        getLastKnownLocation()
//                    }
//                } else {
//                    if (currentLatLng == null) {
//                        // default location
//                        currentLatLng = LatLng(0.0, 0.0)
//                    }
//                }
//            }
//        })
//
//        // location callback listener
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                // process location change
//                processOnLocationChanged(locationResult.lastLocation)
//            }
//        }
//    }
//
//    /**
//     * Method is used to initialize drawer.
//     */
//    private fun initializeDrawer() {
//        // set a Toolbar to act as the ActionBar for this Activity window
//        setSupportActionBar(drawerToolbar)
//        // this class provides a handy way to tie together the functionality of
//        // DrawerLayout and the framework ActionBar to implement the recommended
//        // design for navigation drawers.
//        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            drawerToolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        ) {
//            override fun onDrawerClosed(view: View) {
//                super.onDrawerClosed(view)
//                // TODO add any logic for when drawer is closed e.g. start services
//            }
//
//            override fun onDrawerOpened(drawerView: View) {
//                super.onDrawerOpened(drawerView)
//                // TODO add any logic for when drawer is open e.g. stop services
//            }
//        }
//        drawerLayout?.addDrawerListener(toggle)
//        toggle.syncState()
//
//        // navigation drawer
//        val navigationView: NavigationView = findViewById(R.id.nav_view)
//        // set a listener that will be notified when a menu item is selected
//        navigationView.setNavigationItemSelectedListener(this)
//        // set the tint which is applied to our menu items' icons
//        navigationView.itemIconTintList = null
//    }
//
//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.iv_recenter_map -> {
//                // center camera on my location
//                centerOnMyLocation()
//            }
//        }
//    }
//
//    /**
//     * Method is used to determine whether location permissions have been granted. If no
//     * location permissions are granted, launch [LocationServicesFragment].
//     *
//     * @return Boolean True if location permission and services is enabled, otherwise false.
//     */
//    private fun checkPermissions(): Boolean {
//        val isAppPermissionsEnabled = FrameworkUtils.checkAppPermissions(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//        val isLocationServicesEnabled = FrameworkUtils.isLocationServiceEnabled(
//            this
//        )
//        return isAppPermissionsEnabled && isLocationServicesEnabled
//    }
//
//    /**
//     * Method is used to open and close the drawer.
//     *
//     * @param isOpen True if drawer is open, otherwise false.
//     */
//    fun setDrawerState(isOpen: Boolean) {
//        if (isOpen) {
//            drawerLayout?.openDrawer(drawerLayoutParent)
//        } else {
//            drawerLayout?.closeDrawer(drawerLayoutParent)
//        }
//    }
//
//    /**
//     * Method is used to unlock or lock drawer interaction.
//     *
//     * @param isUnlocked Boolean True to unlock drawer interaction, otherwise false.
//     */
//    fun setDrawerUnlockMode(isUnlocked: Boolean) {
//        if (isUnlocked) {
//            // only unlock drawer interaction if it is disabled
//            if (drawerLayout?.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED) {
//                drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//            }
//        } else {
//            // only allow disabling of drawer interaction if the drawer is closed
//            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        }
//    }
//
//    /**
//     * Method is used to initialize.
//     */
//    private fun setupMap() {
//        // setup map custom style
//        setupMapStyle()
//
//        // setup map configuration
//        initializeMapSettings()
//        // initialize map listeners
//        initializeMapListeners()
//        // current location
//        getLastKnownLocation()
//    }
//
//    /**
//     * Method is used to customize map style.
//     */
//    private fun setupMapStyle() {
//        try {
//            // customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            val success: Boolean =
//                googleMap?.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                        this,
//                        R.raw.style_json
//                    )
//                ) == true
//            if (!success) {
//                Logger.e(Constants.TAG, "style parsing failed")
//            }
//        } catch (e: NotFoundException) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * Method is used to setup map setting configurations.
//     */
//    private fun initializeMapSettings() {
//        // setup map setting configurations
//        mapSettings = googleMap?.uiSettings
//        mapSettings?.isRotateGesturesEnabled = false
//        mapSettings?.isScrollGesturesEnabled = true
//        mapSettings?.isZoomControlsEnabled = false
//        mapSettings?.isMyLocationButtonEnabled = false
//        mapSettings?.isTiltGesturesEnabled = false
//        mapSettings?.isCompassEnabled = false
//        mapSettings?.isIndoorLevelPickerEnabled = false
//        mapSettings?.isMapToolbarEnabled = false
//    }
//
//    /**
//     * Method is used to setup map gestures, scaling and touch listeners.
//     */
//    private fun initializeMapListeners() {
//        // gesture listener
//        gestureDetector = GestureDetector(
//            this, GestureListener(
//                googleMap,
//                object : OnMapTouchListener {
//                    override fun onMapTouch() {
//                        // set visibility
//                        FrameworkUtils.setViewVisible(ivRecenterMap)
//                    }
//                }), null, true
//        )
//        // scale listener
//        scaleDetector = ScaleGestureDetector(
//            this, ScaleGestureListener(
//                googleMap,
//                object : OnMapTouchListener {
//                    override fun onMapTouch() {
//                        // set visibility
//                        FrameworkUtils.setViewVisible(ivRecenterMap)
//                    }
//                }
//            ))
//        // map touch listener (general map movements)
//        mapFragment?.setMapTouchListener(
//            MapTouchListener(
//                this,
//                googleMap,
//                object : OnMapTouchListener {
//                    override fun onMapTouch() {
//                        // set visibility
//                        FrameworkUtils.setViewVisible(ivRecenterMap)
//                    }
//                },
//                gestureDetector,
//                scaleDetector
//            )
//        )
//    }
//
//    /**
//     * Returns the best most recent location currently available.
//     *
//     * <p>If a location is not available, which should happen very rarely, null will
//     * be returned. The best accuracy available while respecting the location permissions
//     * will be returned. This method provides a simplified way to get location. It is
//     * particularly well suited for applications that do not require an accurate location
//     * and that do not want to maintain extra logic for location updates.</p>
//     */
//    private fun getLastKnownLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            // set current location
//            currentLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
//            // start location updates
//            startLocationUpdates()
//        }
//    }
//
//    /**
//     * Method is used to request location updates.
//     *
//     * Ref-https://developer.android.com/training/location/request-updates
//     *
//     * <p>Appropriate use of location information can be beneficial to users of your app.
//     * For example, if your app helps the user find their way while walking or driving, or
//     * if your app tracks the location of assets, it needs to get the location of the device
//     * at regular intervals. As well as the geographical location (latitude and longitude),
//     * you may want to give the user further information such as the bearing (horizontal
//     * direction of travel), altitude, or velocity of the device. This information,
//     * and more, is available in the Location object that your app can retrieve from
//     * the fused location provider.</p>
//     */
//    private fun startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        // display blue dot
//        googleMap?.isMyLocationEnabled = true
//        // center camera on my location
//        centerOnMyLocation()
//        // request location updates
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            Looper.getMainLooper()
//        )
//    }
//
//    /**
//     * Animates the movement of the camera from the current position to the position
//     * defined in the update. During the animation, a call to getCameraPosition()
//     * returns an intermediate location of the camera.
//     */
//    private fun centerOnMyLocation() {
//        // set recenter button visibility
//        FrameworkUtils.setViewGone(ivRecenterMap)
//        // zoom camera on my location
//        googleMap?.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                currentLatLng as LatLng, DEFAULT_ZOOM_LEVEL
//            )
//        )
//    }
//
//    /**
//     * Method is used to process onLocationChanged behaviors.
//     *
//     * <p>Method also tracks GPS signal strength [trackGpsSignalStrength] on Firebase
//     * using an updated location object.</p>
//     *
//     * @param location Location A data class representing a geographic location.
//     */
//    private fun processOnLocationChanged(location: Location) {
//        currentLocation = location
//        currentLatLng = LatLng(location.latitude, location.longitude)
//
//        // track gps strength
//        trackGpsSignalStrength(location)
//    }
//
//    /**
//     * Method is used to track GPS accuracy.
//     *
//     * <p>A GPS strength profile is generated on Firebase analytics.<>
//     *
//     * @param location Location A data class representing a geographic location.
//     */
//    private fun trackGpsSignalStrength(location: Location) {
//        if (location.accuracy <= 0f) {
//            // reset confidence queue
//            alConfidenceQueue.clear()
//        } else if (location.accuracy >= 50f) {
//            // poor signal
//            alConfidenceQueue.add(location.accuracy)
//            if (alConfidenceQueue.size > 3) {
//                // reset confidence queue
//                alConfidenceQueue.clear()
//            }
//        } else {
//            // decent signal
//            if (alConfidenceQueue.size > 0) {
//                alConfidenceQueue.clear()
//            }
//        }
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        var fragment: Fragment? = null
//        val args = Bundle()
//
//        when (item.itemId) {
////            R.id.nav_guide -> fragment = GuideFragment()
////            R.id.nav_settings -> fragment = SettingsFragment()
////            R.id.nav_history -> fragment = HistoryFragment()
////            R.id.nav_share -> fragment = ShareFragment()
////            R.id.nav_about -> fragment = AboutFragment()
//            R.id.nav_privacy -> {
//                // hide navigation bar
//                drawerToolbar?.visibility = View.GONE
//                // set fragment
//                fragment = WebViewFragment()
//                args.putString(Constants.KEY_WEB_VIEW_HEADER, item.toString())
//                args.putString(Constants.KEY_WEB_VIEW_URL, Constants.PRIVACY_URL)
//                fragment.arguments = args
//                fragment.setOnRemoveListener(object : BaseFragment.OnRemoveFragment {
//                    override fun onRemove() {
//                        // show navigation bar
//                        drawerToolbar?.visibility = View.VISIBLE
//                    }
//                })
//            }
//            R.id.nav_terms -> {
//                // hide navigation bar
//                drawerToolbar?.visibility = View.GONE
//                // set fragment
//                fragment = WebViewFragment()
//                args.putString(Constants.KEY_WEB_VIEW_HEADER, item.toString())
//                args.putString(Constants.KEY_WEB_VIEW_URL, Constants.TERMS_URL)
//                fragment.arguments = args
//                fragment.setOnRemoveListener(object : BaseFragment.OnRemoveFragment {
//                    override fun onRemove() {
//                        // show navigation bar
//                        drawerToolbar?.visibility = View.VISIBLE
//                    }
//                })
//            }
//            else -> {
//                // no-op
//            }
//        }
//        // add fragment
//        if (fragment != null) {
//            addFragment(fragment)
//        }
//        // close drawer after selection
//        drawerLayout?.closeDrawer(GravityCompat.START)
//        return false
//    }
//
//    /**
//     * Method is used to destroy map, map settings and all gesture listeners.
//     */
//    private fun destroyMap() {
//        // destroy map, map settings, map gesture listeners
//        googleMap = null
//        mapFragment = null
//        mapSettings = null
//        gestureDetector = null
//        scaleDetector = null
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        // initialize map
//        this.googleMap = googleMap
//
//        // configure map settings
//        setupMap()
//    }
//
//    override fun onLocationChanged(location: Location) {
//        currentLocation = location
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // only register receiver if it has not already been registered
//        if (!networkReceiver.contains(this)) {
//            // register network receiver
//            networkReceiver.addObserver(this)
//            registerReceiver(
//                networkReceiver,
//                IntentFilter(CONNECTIVITY_ACTION)
//            )
//            // print observer list
//            networkReceiver.printObserverList()
//        }
//    }
//
//    override fun onPause() {
//        // unregister network receiver (observer)
//        if (networkReceiver.getObserverSize() > 0 && networkReceiver.contains(this)) {
//            try {
//                // unregister network receiver
//                unregisterReceiver(networkReceiver)
//            } catch (e: IllegalStateException) {
//                Logger.e(Constants.TAG, e.message.orEmpty(), e)
//                e.printStackTrace()
//            }
//            networkReceiver.removeObserver(this)
//        }
//        super.onPause()
//    }
//
//    override fun onDestroy() {
//        // destroy map, map settings, map gesture listeners
//        destroyMap()
//        super.onDestroy()
//    }
//
//    override fun onBackPressed() {
//        // handle device back button various states
//        if (drawerLayout?.isDrawerOpen(drawerLayoutParent) == true) {
//            // if drawer is open, close it
//            setDrawerState(false);
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    override fun notifyConnectionChange(isConnected: Boolean) {
//        if (isConnected) {
//            // app is connected to network
//            dialog.dismissNoNetworkDialog()
//        } else {
//            // app is not connected to network
//            dialog.showNoNetworkDialog(
//                this,
//                resources.getString(R.string.dialog_title_check_network),
//                resources.getString(R.string.dialog_check_network)
//            )
//        }
//    }
//}
