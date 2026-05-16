package ru.mirea.mvolobueva.mireaproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.mirea.mvolobueva.mireaproject.R

class PlacesFragment : Fragment(R.layout.fragment_places) {

    private lateinit var mapView: MapView
    private var locationOverlay: MyLocationNewOverlay? = null
    private var compassOverlay: CompassOverlay? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            setupUserLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            requireContext().applicationContext,
            PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        )
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapViewPlaces)

        setupMap()
        setupCompass()
        setupScaleBar()
        addPlacesMarkers()
        checkLocationPermissions()
    }

    private fun setupMap() {
        mapView.setZoomRounding(true)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(13.0)

        val centerPoint = GeoPoint(55.751244, 37.618423)
        mapController.setCenter(centerPoint)
    }

    private fun checkLocationPermissions() {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            setupUserLocation()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupUserLocation() {
        if (locationOverlay != null) return

        locationOverlay = MyLocationNewOverlay(
            GpsMyLocationProvider(requireContext().applicationContext),
            mapView
        )
        locationOverlay?.enableMyLocation()
        mapView.overlays.add(locationOverlay)
    }

    private fun setupCompass() {
        compassOverlay = CompassOverlay(
            requireContext().applicationContext,
            InternalCompassOrientationProvider(requireContext().applicationContext),
            mapView
        )
        compassOverlay?.enableCompass()
        mapView.overlays.add(compassOverlay)
    }

    private fun setupScaleBar() {
        val dm: DisplayMetrics = resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10)
        mapView.overlays.add(scaleBarOverlay)
    }

    private fun addPlacesMarkers() {
        addPlaceMarker(
            GeoPoint(55.670005, 37.479894),
            "РТУ МИРЭА",
            "Проспект Вернадского, 78",
            "Учебный корпус университета"
        )

        addPlaceMarker(
            GeoPoint(55.751244, 37.618423),
            "Красная площадь",
            "Москва, Красная площадь",
            "Историческое место и туристическая достопримечательность"
        )

        addPlaceMarker(
            GeoPoint(55.760186, 37.618711),
            "Большой театр",
            "Театральная площадь, 1",
            "Известный театр и культурное место"
        )

        addPlaceMarker(
            GeoPoint(55.729876, 37.603123),
            "Парк Горького",
            "ул. Крымский Вал, 9",
            "Популярное место для прогулок и отдыха"
        )
    }

    private fun addPlaceMarker(
        point: GeoPoint,
        title: String,
        address: String,
        description: String
    ) {
        val marker = Marker(mapView)
        marker.position = point
        marker.title = title
        marker.subDescription = "$address\n$description"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        marker.icon = ResourcesCompat.getDrawable(
            resources,
            org.osmdroid.library.R.drawable.osm_ic_follow_me_on,
            null
        )

        marker.setOnMarkerClickListener { selectedMarker, _ ->
            Toast.makeText(
                requireContext(),
                "${selectedMarker.title}\n${selectedMarker.subDescription}",
                Toast.LENGTH_LONG
            ).show()
            true
        }

        mapView.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(
            requireContext().applicationContext,
            PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        )
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        Configuration.getInstance().save(
            requireContext().applicationContext,
            PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        )
        mapView.onPause()
    }
}