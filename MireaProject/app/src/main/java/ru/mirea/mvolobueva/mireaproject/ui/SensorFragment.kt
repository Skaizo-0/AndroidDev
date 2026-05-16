package ru.mirea.mvolobueva.mireaproject.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.mirea.mvolobueva.mireaproject.R
/*
КРАТКОЕ ОПИСАНИЕ:
Фрагмент для работы с акселерометром (датчиком ускорения) устройства.
Отображает текущие значения ускорения по трем осям (X, Y, Z) и определяет
примерное положение телефона на основе этих данных.

*/
class SensorFragment : Fragment(R.layout.fragment_sensor), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var textX: TextView
    private lateinit var textY: TextView
    private lateinit var textZ: TextView
    private lateinit var textState: TextView
    // onViewCreated  СОЗДАНИЕ UI И ИНИЦИАЛИЗАЦИЯ ДАТЧИКОВ
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textX = view.findViewById(R.id.textViewX)
        textY = view.findViewById(R.id.textViewY)
        textZ = view.findViewById(R.id.textViewZ)
        textState = view.findViewById(R.id.textViewState)
// Получение системного сервиса SENSOR_SERVICE
        // requireActivity() - получаем активность, в которой находится фрагмент
        // getSystemService() - получаем системный сервис по типу

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Получение датчика акселерометра по умолчанию
        // TYPE_ACCELEROMETER - тип датчика (измеряет ускорение)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            textState.text = "Акселерометр отсутствует на устройстве"
        }
    }
    // onResume  РЕГИСТРАЦИЯ СЛУШАТЕЛЯ
    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    // onSensorChanged  ОБРАБОТКА НОВЫХ ДАННЫХ ОТ ДАТЧИКА
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
// ОТОБРАЖЕНИЕ ЗНАЧЕНИЙ НА ЭКРАНЕ
        textX.text = "X: %.2f".format(x)
        textY.text = "Y: %.2f".format(y)
        textZ.text = "Z: %.2f".format(z)
// ОПРЕДЕЛЕНИЕ ПОЛОЖЕНИЯ ТЕЛЕФОНА
        textState.text = when {
            x > 2 -> "Телефон наклонён влево"
            x < -2 -> "Телефон наклонён вправо"
            z > 9 -> "Телефон лежит почти ровно экраном вверх"
            else -> "Положение устройства изменяется"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}