package ru.mirea.mvolobueva.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var textViewAzimuth: TextView
    private lateinit var textViewPitch: TextView
    private lateinit var textViewRoll: TextView

    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewAzimuth = findViewById(R.id.textViewAzimuth)
        textViewPitch = findViewById(R.id.textViewPitch)
        textViewRoll = findViewById(R.id.textViewRoll)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometerSensor == null) {
            textViewAzimuth.text = "Акселерометр отсутствует"
            textViewPitch.text = ""
            textViewRoll.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.also { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val valueX = event.values[0]
            val valueY = event.values[1]
            val valueZ = event.values[2]

            textViewAzimuth.text = "X: %.2f".format(valueX)
            textViewPitch.text = "Y: %.2f".format(valueY)
            textViewRoll.text = "Z: %.2f".format(valueZ)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}