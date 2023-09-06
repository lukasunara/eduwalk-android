package hr.eduwalk.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.data.model.Location
import hr.eduwalk.databinding.FragmentWalkBinding
import hr.eduwalk.ui.event.RouteEvent
import hr.eduwalk.ui.viewmodel.RouteViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RouteFragment :
    BaseFragment(contentLayoutId = R.layout.fragment_walk),
    OnMapReadyCallback,
    OnMapLoadedCallback,
    OnMapLongClickListener,
    OnMarkerClickListener,
    OnMarkerDragListener {

    override val viewModel: RouteViewModel by viewModels()

    private val args: WalkFragmentArgs by navArgs()

    private var isCollecting = false
    private var areMarkersEnabled = false
    private var binding: FragmentWalkBinding? = null
    private var googleMap: GoogleMap? = null
    private var markers = mutableListOf<Marker>()

    private lateinit var mapView: MapView

    private val markerIcon: BitmapDescriptor by lazy {
        ContextCompat
            .getDrawable(requireContext(), R.drawable.ic_marker_default)
            ?.toBitmap()
            ?.let { bitmap -> BitmapDescriptorFactory.fromBitmap(bitmap) }
            ?: BitmapDescriptorFactory.defaultMarker()
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
        viewModel.start(walk = args.walk)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isCollecting = false
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun setupUi() {
        binding?.apply {
            toolbar.apply {
                buttonOption1.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))
                }
                buttonOption2.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete))
                }
            }
        }
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            toolbar.apply {
                backButton.setOnClickListener { navController.popBackStack() }
                buttonOption1.setOnClickListener { viewModel.onEditWalkInfoClicked() }
                buttonOption2.setOnClickListener { showAlertDialog() }
            }
        }
    }

    override fun setupObservers() {
        if (isCollecting) return
        super.setupObservers()
        lifecycleScope.launch {
            isCollecting = true
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is RouteEvent.FinishRouteFragment -> navController.popBackStack()
                    is RouteEvent.NavigateToEditWalkInfo -> {
                        navController.navigate(directions = RouteFragmentDirections.navigateToEditWalkInfoFragment(walk = event.walk))
                    }
                    is RouteEvent.ShowLocations -> updateLocations(locations = event.locations)
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { uiState ->
                updateLocations(locations = uiState.locations)
                binding?.toolbar?.toolbarTitle?.apply {
                    isVisible = true
                    text = uiState.walk?.title.orEmpty()
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
            setOnMarkerClickListener(this@RouteFragment)
            setOnMarkerDragListener(this@RouteFragment)
            setOnMapLoadedCallback(this@RouteFragment)
        }
    }

    override fun onMapLoaded() = viewModel.onMapLoaded()

    override fun onMapLongClick(latLng: LatLng) {
        val location = Location(
            id = -1,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            title = "",
            description = null,
            imageBase64 = null,
            thresholdDistance = 20,
            walkId = args.walk.id,
        )
        navController.navigate(directions = RouteFragmentDirections.navigateToEditLocationInfoFragment(location = location))
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (!areMarkersEnabled) return true

        val positionLatLng = LatLng(marker.position.latitude, marker.position.longitude)
        googleMap?.apply {
            animateCamera(CameraUpdateFactory.newLatLng(positionLatLng))
        }
        val location = marker.tag as Location

        navController.navigate(directions = RouteFragmentDirections.navigateToEditLocationInfoFragment(location = location))

        return true
    }

    override fun onMarkerDrag(marker: Marker) {} // no-op

    override fun onMarkerDragEnd(marker: Marker) {
        val location = marker.tag as Location

        viewModel.onMarkerDragged(
            oldLocation = location,
            newLatitude = marker.position.latitude,
            newLongitude = marker.position.longitude,
        )
    }

    override fun onMarkerDragStart(marker: Marker) {
        areMarkersEnabled = false
    }

    private fun updateLocations(locations: List<Location>) {
        val googleMap = googleMap ?: return

        markers.forEach { it.remove() }.also { markers.clear() }

        locations.forEach { location ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .icon(markerIcon)
                    .draggable(true)
            )?.apply { tag = location }

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

        areMarkersEnabled = true
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.do_you_want_to_delete_route)
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.onDeleteWalkClicked() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private companion object {
        const val MIN_ZOOM = 7f
        const val BOUNDS_THRESHOLD = 0.0007

        // Croatia bounds
        const val DEFAULT_WEST_LONGITUDE = 13.5
        const val DEFAULT_EAST_LONGITUDE = 19.4
        const val DEFAULT_SOUTH_LATITUDE = 42.3
        const val DEFAULT_NORTH_LATITUDE = 46.5
    }
}
