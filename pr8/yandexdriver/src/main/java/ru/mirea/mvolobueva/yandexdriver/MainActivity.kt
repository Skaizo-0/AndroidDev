package ru.mirea.mvolobueva.yandexdriver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider

class MainActivity : AppCompatActivity(), DrivingSession.DrivingRouteListener {

    private lateinit var mapView: MapView
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var drivingRouter: DrivingRouter
    private var drivingSession: DrivingSession? = null

    private val destinationPoint = Point(55.794229, 37.700772)

    private var startPoint = Point(55.670005, 37.479894)

    private val colors = listOf(
        0xFFFF0000.toInt(),
        0xFF00FF00.toInt(),
        0xFF0000FF.toInt(),
        0xFF00FFFF.toInt()
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                buildRoutes()
            } else {
                Toast.makeText(this, "Разрешение на геолокацию не выдано", Toast.LENGTH_SHORT).show()
                buildRoutes()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        DirectionsFactory.initialize(this)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapview)
        mapView.map.isRotateGesturesEnabled = false

        val screenCenter = Point(
            (startPoint.latitude + destinationPoint.latitude) / 2,
            (startPoint.longitude + destinationPoint.longitude) / 2
        )

        mapView.map.move(
            CameraPosition(screenCenter, 10.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = mapView.map.mapObjects.addCollection()

        addDestinationMarker()
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
            buildRoutes()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun buildRoutes() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()

        drivingOptions.routesCount = 4

        val requestPoints = arrayListOf<RequestPoint>(
            RequestPoint(startPoint, RequestPointType.WAYPOINT, null),
            RequestPoint(destinationPoint, RequestPointType.WAYPOINT, null)
        )

        drivingSession = drivingRouter.requestRoutes(
            requestPoints,
            drivingOptions,
            vehicleOptions,
            this
        )
    }

    private fun addDestinationMarker() {
        val marker = mapView.map.mapObjects.addPlacemark(
            destinationPoint,
            ImageProvider.fromResource(this, android.R.drawable.star_big_on)
        )

        marker.addTapListener(object : MapObjectTapListener {
            override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
                Toast.makeText(
                    this@MainActivity,
                    "Любимое заведение:\nАдрес: Москва\nКраткое описание: любимое место для отдыха",
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
        })
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        for (i in routes.indices) {
            val color = colors[i % colors.size]
            mapObjects.addPolyline(routes[i].geometry).setStrokeColor(color)
        }
    }

    override fun onDrivingRoutesError(error: Error) {
        Toast.makeText(this, "Ошибка построения маршрута", Toast.LENGTH_SHORT).show()
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