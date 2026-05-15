package ru.mirea.mvolobueva.yandexmaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.RotationType

class MainActivity : AppCompatActivity(), UserLocationObjectListener {

    private lateinit var mapView: MapView
    private var userLocationLayer: UserLocationLayer? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                loadUserLocationLayer()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapview)

        mapView.map.move(
            CameraPosition(
                Point(55.751574, 37.573856),
                11.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )

        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            loadUserLocationLayer()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun loadUserLocationLayer() {
        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()

        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer?.isVisible = true
        userLocationLayer?.isHeadingEnabled = true
        userLocationLayer?.setObjectListener(this)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer?.setAnchor(
            PointF(
                mapView.width * 0.5f,
                mapView.height * 0.5f
            ),
            PointF(
                mapView.width * 0.5f,
                mapView.height * 0.83f
            )
        )

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(this, android.R.drawable.arrow_up_float)
        )

        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(this, android.R.drawable.ic_menu_mylocation),
            IconStyle()
                .setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(1.0f)
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and 0x99ffffff.toInt()
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {
    }

    override fun onObjectUpdated(
        userLocationView: UserLocationView,
        objectEvent: ObjectEvent
    ) {
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}