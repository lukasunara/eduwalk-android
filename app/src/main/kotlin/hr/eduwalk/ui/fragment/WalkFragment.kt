package hr.eduwalk.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.data.model.LocationWithScore
import hr.eduwalk.databinding.FragmentWalkBinding
import hr.eduwalk.ui.dialog.PermissionRationaleDialog
import hr.eduwalk.ui.dialog.WalkInfoDialog
import hr.eduwalk.ui.event.WalkEvent
import hr.eduwalk.ui.viewmodel.WalkViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalkFragment : BaseFragment(contentLayoutId = R.layout.fragment_walk), OnMapReadyCallback, OnMarkerClickListener {

    override val viewModel: WalkViewModel by viewModels()

    private val args: WalkFragmentArgs by navArgs()

    private var binding: FragmentWalkBinding? = null
    private var markers = mutableListOf<Marker>()

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        LOCATION_UPDATE_INTERVAL_IN_MILLISECONDS,
    ).build()

    private val locationSettingsRequest: LocationSettingsRequest by lazy {
        LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
    }
    private var areLocationRequestsEnabled = false
    private var currentLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val permissions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentWalkBinding.inflate(inflater, container, false)
        this.binding = binding

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        createLocationCallback()
        viewModel.start(walkId = args.walk.id)
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun setupUi() {
        binding?.apply {
            toolbar.apply {
                toolbarTitle.apply {
                    isVisible = true
                    text = args.walk.title
                }
                buttonOption1.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_info))
                }
                buttonOption2.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_leaderboard))
                }
            }
        }
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            toolbar.apply {
                backButton.setOnClickListener { navController.popBackStack() }
                buttonOption1.setOnClickListener {
                    WalkInfoDialog(context = requireContext(), walk = args.walk).show()
                }
                buttonOption2.setOnClickListener {
                    // todo: show leaderboard dialog
                }
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        lifecycleScope.launch {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is WalkEvent.ShowLocations -> updateLocations(locationsWithScores = event.locationsWithScores)
                    is WalkEvent.UpdateScore -> binding?.toolbar?.toolbarSubtitle?.apply {
                        isVisible = true
                        text = getString(R.string.walk_score, event.total, event.max)
                    }
                    null -> {
                        // no-op
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap.apply {
            setMinZoomPreference(MIN_ZOOM)
            uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMapToolbarEnabled = false
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // todo: show bottom sheet
        return true
    }

    private fun updateLocations(locationsWithScores: List<LocationWithScore>) {
        locationsWithScores.forEach { locationWithScore ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(locationWithScore.location.latitude, locationWithScore.location.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(chooseColor(locationWithScore.score)))
                    .alpha(MARKER_DISABLED_ALPHA)
            )?.apply { tag = locationWithScore.location }

            marker?.let { markers.add(it) }
        }

        val swBounds = LatLng(
            (markers.minOfOrNull { it.position.latitude } ?: DEFAULT_SOUTH_LATITUDE) - BOUNDS_THRESHOLD,
            (markers.minOfOrNull { it.position.longitude } ?: DEFAULT_WEST_LONGITUDE) - BOUNDS_THRESHOLD,
        )
        val neBounds = LatLng(
            (markers.maxOfOrNull { it.position.latitude } ?: DEFAULT_NORTH_LATITUDE) + BOUNDS_THRESHOLD,
            (markers.maxOfOrNull { it.position.longitude } ?: DEFAULT_EAST_LONGITUDE) + BOUNDS_THRESHOLD,
        )
        val bounds = LatLngBounds(swBounds, neBounds)
        googleMap.setLatLngBoundsForCameraTarget(bounds)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
    }

    private fun chooseColor(score: Int?): Float = when (score) {
        0 -> BitmapDescriptorFactory.HUE_RED
        1 -> BitmapDescriptorFactory.HUE_ORANGE
        2 -> BitmapDescriptorFactory.HUE_YELLOW
        3 -> BitmapDescriptorFactory.HUE_GREEN
        null -> BitmapDescriptorFactory.HUE_CYAN
        else -> BitmapDescriptorFactory.HUE_VIOLET
    }

    private fun checkPermissions() {
        permissionRequest.launch(permissions.toTypedArray())
    }

    private fun onPermissionDenied() {
        val (showEnablePermissionText, onRationaleButtonClickListener) =
            if (this.permissions.all { shouldShowRequestPermissionRationale(it) }) {
                Pair(false) { checkPermissions() }
            } else {
                Pair(true) { openAppSettings() }
            }
        PermissionRationaleDialog(
            context = requireContext(),
            showEnablePermissionText = showEnablePermissionText,
            onRationaleButtonClicked = onRationaleButtonClickListener,
        ).show()
    }

    @SuppressLint("MissingPermission")
    private fun onPermissionGranted() {
        if (isLocationEnabled()) {
            if (!areLocationRequestsEnabled) checkLocationSettings()
            lifecycleScope.launch {
                delay(CHECK_LOCATION_SETTINGS_DELAY)
                getLastLocation()
                if (areLocationRequestsEnabled) startLocationUpdates()
                googleMap.isMyLocationEnabled = true
            }
        } else {
            PermissionRationaleDialog(
                context = requireContext(),
                showEnablePermissionText = true,
                onRationaleButtonClicked = { openLocationSettings() },
            ).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location -> currentLocation = location }
            .addOnFailureListener { e -> Log.e(this::class.java.simpleName, e.toString()) }
    }

    private fun checkLocationSettings() {
        LocationServices
            .getSettingsClient(requireContext())
            .checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                // All location settings are satisfied. The client can initialize location requests here.
                Log.d(this::class.java.simpleName, "All location settings are satisfied.")
                areLocationRequestsEnabled = true
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                if (e is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
//                        e.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
//                Log.d("SUKI", "lastLocation: ${locationResult.lastLocation}")
//                locationResult.lastLocation?.let { lastLocation ->
//                    map?.apply {
//                        animateCamera(
//                            CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), ZOOM_LEVEL)
//                        )
//                    }
//                    MARKERS_LAT_LNG_LIST.forEach { (title, latLng) ->
//                        val markerLocation = Location(title).apply {
//                            latitude = latLng.latitude
//                            longitude = latLng.longitude
//                        }
//                        val distance = markerLocation.distanceTo(lastLocation)
//                        Log.d("SUKI", "Distance from last location to $title is $distance m")
//                    }
//                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private companion object {
        const val MIN_ZOOM = 15f
        const val BOUNDS_THRESHOLD = 0.0007
        const val MARKER_DISABLED_ALPHA = 0.7f
        const val CHECK_LOCATION_SETTINGS_DELAY = 1000L
        const val LOCATION_UPDATE_INTERVAL_IN_MILLISECONDS = 10000L

        // Croatia bounds
        const val DEFAULT_WEST_LONGITUDE = 13.5
        const val DEFAULT_EAST_LONGITUDE = 19.4
        const val DEFAULT_SOUTH_LATITUDE = 42.3
        const val DEFAULT_NORTH_LATITUDE = 46.5
    }
}
