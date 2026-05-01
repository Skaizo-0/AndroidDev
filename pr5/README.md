

# Отчет по практическому занятию № 5
## «Работа с аппаратными ресурсами смартфона»

### Содержание
1. Цель работы
2. Задачи практики
3. Краткие теоретические сведения
4. Ход выполнения работы
   * 4.1. Задание 1. Список датчиков
   * 4.2. Задание 2. Акселерометр
   * 4.3. Задание 3. Механизм разрешений
   * 4.4. Задание 4. Камера
   * 4.5. Задание 5. Микрофон
   * 4.6. Контрольное задание в MireaProject
5. Вывод

---

### 1. Цель работы
Основной целью данной работы является глубокое изучение архитектурных решений ОС Android для взаимодействия с аппаратным обеспечением мобильного устройства. В ходе работы необходимо освоить:
* Методы получения доступа к системным сервисам управления датчиками;
* Алгоритмы считывания и обработки данных с акселерометра в реальном времени;
* Современные стандарты безопасности Android, включая реализацию механизма динамических разрешений (Runtime Permissions);
* Принципы интеграции мультимедийных возможностей: захват изображений через системную камеру и работа с аудиопотоками;
* Процесс консолидации различных аппаратных модулей в рамках комплексного приложения `MireaProject`.

---

### 2. Задачи практики
Для достижения поставленной цели в рамках практического занятия необходимо выполнить следующий ряд задач:
1. Разработать базовый проект `Lesson5` для программного определения всех доступных на конкретном устройстве аппаратных сенсоров и вывода их характеристик.
2. Спроектировать и реализовать отдельный модуль `Accelerometer`, обеспечивающий визуализацию векторов ускорения по трем пространственным осям.
3. Детально изучить жизненный цикл запроса разрешений и внедрить его в функционал работы с камерой и микрофоном для обеспечения корректной работы на актуальных версиях Android.
4. Создать функциональный модуль `Camera`, позволяющий инициировать захват изображения, организовать безопасное хранение полученного файла через `FileProvider` и отобразить результат пользователю.
5. Реализовать модуль `AudioRecord` для захвата звукового сигнала с микрофона, сохранения аудиофайла и его последующего воспроизведения с использованием медиа-библиотек.
6. В качестве контрольного этапа — интегрировать функционал датчиков, камеры и микрофона в итоговый проект `MireaProject` в виде независимых фрагментов.

---

### 3. Краткие теоретические сведения
Взаимодействие с сенсорной подсистемой Android осуществляется через системный сервис `SensorManager`. Этот класс предоставляет доступ к списку доступных датчиков и позволяет настраивать параметры получения данных (например, частоту обновления). Для прослушивания событий изменения состояния датчиков необходимо реализовать интерфейс `SensorEventListener`, методы которого вызываются системой при появлении новых данных или изменении точности сенсора. В случае с акселерометром, данные приходят в виде массива из трех значений, представляющих ускорение по осям X, Y и Z в м/с². При нахождении устройства в покое на поверхности, датчик фиксирует силу тяжести (около 9.81 м/с² по вертикальной оси).

Безопасность персональных данных пользователя обеспечивается системой разрешений. С выходом Android 6.0 и выше разработчики обязаны запрашивать «опасные» разрешения (такие как доступ к камере или микрофону) непосредственно в момент использования функции, а не только при установке приложения. Этот процесс включает проверку текущего статуса через `ContextCompat.checkSelfPermission` и обработку обратного вызова от системы.

Для работы с фотокамерой наиболее эффективным способом является делегирование задачи системному приложению через намерение `MediaStore.ACTION_IMAGE_CAPTURE`. Важным аспектом здесь является использование `FileProvider` — специального компонента, который позволяет передавать URI файла другим приложениям безопасным способом, исключая прямую передачу путей файловой системы.

Запись аудиоинформации реализуется с помощью класса `MediaRecorder`, который инкапсулирует в себе управление источником звука, кодеками и выходным форматом (например, MPEG_4). Для обратной операции — воспроизведения — используется класс `MediaPlayer`, обеспечивающий управление потоком данных и состоянием проигрывателя.

---

### 4. Ход выполнения работы

#### 4.1. Задание 1. Список датчиков
В рамках первого задания был создан проект `ru.mirea.mvolobueva.lesson5`. Целью данного этапа было освоение работы с `SensorManager` и вывод динамического списка доступных компонентов устройства. В качестве графического интерфейса использовался компонент `ListView`, данные в который передаются через `SimpleAdapter`.

**Листинг activity_main.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ListView
        android:id="@+id/sensorListView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="8dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_of="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Листинг MainActivity.kt**
```kotlin
package ru.mirea.mvolobueva.lesson5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import ru.mirea.mvolobueva.lesson5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val data = ArrayList<HashMap<String, String>>()

        for (sensor in sensors) {
            val item = HashMap<String, String>()
            item["Name"] = sensor.name
            item["Value"] = "Макс. диапазон: ${sensor.maximumRange}"
            data.add(item)
        }

        val adapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf("Name", "Value"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        binding.sensorListView.adapter = adapter
    }
}
```
**Результат:** Было разработано приложение, которое при запуске опрашивает систему и выводит полный перечень всех аппаратных датчиков, включая их технические параметры (максимальный диапазон измерений).

---

#### 4.2. Задание 2. Акселерометр
Во втором задании был реализован модуль для работы с датчиком ускорения. Основной акцент был сделан на правильное управление жизненным циклом: регистрация слушателя (`registerListener`) происходит в `onResume`, а его отключение (`unregisterListener`) — в `onPause`, что критически важно для экономии заряда батареи устройства.

**Листинг activity_main.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textViewAzimuth"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="16dp"
        android:text="X: "
        android:textSize="22sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/textViewPitch"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="20dp"
        android:layout_marginEnd="16dp"
        android:text="Y: "
        android:textSize="22sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewAzimuth" />

    <TextView
        android:id="@+id/textViewRoll"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="20dp"
        android:layout_marginEnd="16dp"
        android:text="Z: "
        android:textSize="22sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewPitch" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Листинг MainActivity.kt**
```kotlin
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
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
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
```
**Результат:** Реализовано приложение, отслеживающее пространственное положение смартфона. Данные обновляются в реальном времени, позволяя фиксировать малейшие наклоны и перемещения устройства.



#### 4.3. Задание 3. Механизм разрешений
В современных версиях ОС Android (начиная с API 23) безопасность пользовательских данных обеспечивается через систему Runtime Permissions. В рамках данного задания был реализован универсальный алгоритм запроса доступа к аппаратным ресурсам. Логика работы включает три ключевых этапа:
1.  **Проверка:** Использование `ContextCompat.checkSelfPermission` для определения, было ли разрешение выдано ранее.
2.  **Запрос:** Вызов `ActivityCompat.requestPermissions`, который инициирует системное диалоговое окно для взаимодействия с пользователем.
3.  **Обработка ответа:** Переопределение метода `onRequestPermissionsResult`, где анализируется массив `grantResults`. Если пользователь подтвердил запрос, флаг `isWork` переводится в состояние `true`, разрешая дальнейшую работу с оборудованием.

**Листинг шаблона запроса разрешения**
```kotlin
private fun checkPermissions() {
    val permissionStatus = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    )

    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        isWork = true
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_PERMISSION
        )
    }
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == REQUEST_CODE_PERMISSION) {
        isWork = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}
```
Данный механизм является фундаментальным для стабильной работы приложения, так как попытка доступа к камере или микрофону без подтвержденного разрешения приведет к аварийному завершению программы (SecurityException).

---

#### 4.4. Задание 4. Камера
Целью задания была реализация модуля `Camera` для захвата изображений. Для взаимодействия с системной камерой использовался современный программный интерфейс `ActivityResultLauncher`, который пришел на смену устаревшему `startActivityForResult`. Одной из критических задач на этом этапе стала настройка `FileProvider`, который необходим для безопасной передачи URI созданного файла приложению камеры. Это предотвращает возникновение ошибки `FileUriExposedException`, возникающей при передаче путей файловой системы напрямую.

**Листинг AndroidManifest.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.CAMERA" />

    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />

    <application
        android:allowBackup="true"
        android:supportsRtl="true"
        android:theme="@style/Theme.Lesson5">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/paths" />
        </provider>

    </application>

</manifest>
```

**Листинг res/xml/paths.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path
        name="images"
        path="Pictures" />
</paths>
```

**Листинг activity_main.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="16dp"
        android:background="#DDDDDD"
        android:contentDescription="Фото"
        android:scaleType="centerCrop"
        android:src="@android:drawable/ic_menu_camera"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Листинг MainActivity.kt**
```kotlin
package ru.mirea.mvolobueva.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ru.mirea.mvolobueva.camera.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Разрешение на камеру не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let { binding.imageView.setImageURI(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setOnClickListener {
            checkPermissionAndOpenCamera()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val photoFile = createImageFile()
            val authorities = "${applicationContext.packageName}.fileprovider"
            imageUri = FileProvider.getUriForFile(this, authorities, photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraLauncher.launch(intent)
        } catch (e: IOException) {
            Toast.makeText(this, "Ошибка создания файла", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }
}
```
**Результат:** В ходе выполнения задания было реализовано полнофункциональное взаимодействие с камерой. Метод `createImageFile` формирует уникальное имя файла на основе текущей метки времени, что исключает перезапись старых снимков. Полученное изображение автоматически отображается в `ImageView` после подтверждения съемки.

---

#### 4.5. Задание 5. Микрофон
В завершающем задании основного цикла был разработан модуль `AudioRecord`. Приложение функционирует как цифровой диктофон. Для реализации процесса записи использовался класс `MediaRecorder`, который требует строгой последовательности конфигурации: установка источника звука (`MIC`), формата контейнера (`MPEG_4`) и кодека (`AAC`). Для воспроизведения сохраненного контента применяется класс `MediaPlayer`.

**Листинг AndroidManifest.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:allowBackup="true"
        android:supportsRtl="true"
        android:theme="@style/Theme.Lesson5">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

**Листинг activity_main.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <Button
        android:id="@+id/btnRecord"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="24dp"
        android:layout_marginTop="120dp"
        android:layout_marginEnd="24dp"
        android:text="Начать запись"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/btnPlay"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="24dp"
        android:layout_marginTop="24dp"
        android:layout_marginEnd="24dp"
        android:text="Воспроизвести"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/btnRecord" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Листинг MainActivity.kt**
```kotlin
package ru.mirea.mvolobueva.audiorecord

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 200
    }

    private lateinit var btnRecord: Button
    private lateinit var btnPlay: Button

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isRecording = false
    private var isPlaying = false
    private var isWork = false

    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.btnRecord)
        btnPlay = findViewById(R.id.btnPlay)

        fileName = "${externalCacheDir?.absolutePath}/voice_note.m4a"

        checkPermissions()
        updateButtons()

        btnRecord.setOnClickListener {
            if (!isWork) {
                checkPermissions()
                Toast.makeText(this, "Разрешение на микрофон не выдано", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRecording) stopRecording() else startRecording()
            updateButtons()
        }

        btnPlay.setOnClickListener {
            if (isPlaying) stopPlaying() else startPlaying()
            updateButtons()
        }
    }

    private fun checkPermissions() {
        val audioPermissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (audioPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            isWork = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startRecording() {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(fileName)
            prepare()
            start()
        }

        isRecording = true
        Toast.makeText(this, "Запись началась", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        Toast.makeText(this, "Запись остановлена", Toast.LENGTH_SHORT).show()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(fileName)
            prepare()
            start()
            setOnCompletionListener {
                stopPlaying()
                updateButtons()
            }
        }

        isPlaying = true
        Toast.makeText(this, "Воспроизведение началось", Toast.LENGTH_SHORT).show()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        isPlaying = false
        Toast.makeText(this, "Воспроизведение остановлено", Toast.LENGTH_SHORT).show()
    }

    private fun updateButtons() {
        btnRecord.text = if (isRecording) "Остановить запись" else "Начать запись"
        btnPlay.text = if (isPlaying) "Остановить воспроизведение" else "Воспроизвести"

        btnPlay.isEnabled = !isRecording
        btnRecord.isEnabled = !isPlaying
    }

    override fun onStop() {
        super.onStop()

        if (isRecording) stopRecording()
        if (isPlaying) stopPlaying()

        updateButtons()
    }
}
```
**Результат:** Разработано приложение, позволяющее осуществлять аудиозапись с автоматическим сохранением файла во внешний кеш приложения. Особое внимание было уделено управлению ресурсами: метод `onStop` гарантирует освобождение ресурсов `MediaRecorder` и `MediaPlayer` при выходе из приложения или переходе в фоновый режим, что предотвращает утечки памяти и блокировку оборудования. Метод `updateButtons` обеспечивает корректную логику интерфейса, блокируя возможность воспроизведения во время записи и наоборот.
Я завершил переработку финальной части отчета. Описания разделов значительно расширены, добавлены технические подробности о работе с `Fragments`, жизненном цикле компонентов и управлении ресурсами. Код оставлен в исходном виде, как и требовалось.

---

#### 4.6. Контрольное задание в MireaProject
Заключительным этапом работы стала интеграция изученных технологий в комплексный проект `MireaProject`. Основная задача заключалась в создании модульной архитектуры на базе фрагментов (`Fragments`), каждый из которых инкапсулирует логику работы с конкретным аппаратным ресурсом. Было реализовано три функциональных экрана, решающих прикладные задачи:

1.  **`SensorFragment`**: Реализует логику анализа пространственного положения устройства с использованием акселерометра. В отличие от базового задания, здесь добавлена интерпретация «сырых» данных в человекочитаемые состояния (наклон влево/вправо/горизонтальное положение), что демонстрирует практическое применение датчиков в пользовательских интерфейсах.
2.  **`CameraFragment`**: Представляет собой модуль «Фото-заметка». В данном фрагменте реализован полный цикл работы с изображением: от запроса динамических разрешений до генерации временного файла через `FileProvider`. Пользователь получает возможность не только сделать снимок, но и сопроводить его текстовым комментарием, что имитирует работу реальных приложений для заметок.
3.  **`AudioFragment`**: Реализует функционал «Голосовой заметки». Здесь особое внимание уделено состоянию интерфейса (`UI State`): кнопки блокируются в зависимости от текущего процесса (запись или воспроизведение), что предотвращает конфликты доступа к аудио-драйверу.

**Пример SensorFragment.kt**
```kotlin
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

class SensorFragment : Fragment(R.layout.fragment_sensor), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var textX: TextView
    private lateinit var textY: TextView
    private lateinit var textZ: TextView
    private lateinit var textState: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textX = view.findViewById(R.id.textViewX)
        textY = view.findViewById(R.id.textViewY)
        textZ = view.findViewById(R.id.textViewZ)
        textState = view.findViewById(R.id.textViewState)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            textState.text = "Акселерометр отсутствует на устройстве"
        }
    }

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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        textX.text = "X: %.2f".format(x)
        textY.text = "Y: %.2f".format(y)
        textZ.text = "Z: %.2f".format(z)

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
```
*Пояснение к коду:* В данном фрагменте используется `requireActivity()` для доступа к системному сервису, так как фрагмент не является самостоятельным контекстом. Логика в методе `onSensorChanged` использует пороговые значения (2 м/с²), чтобы отсечь незначительные колебания и шумы датчика, обеспечивая стабильный вывод текста.

**Пример CameraFragment.kt**
```kotlin
package ru.mirea.mvolobueva.mireaproject.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ru.mirea.mvolobueva.mireaproject.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var imageViewPhoto: ImageView
    private lateinit var editTextNote: EditText
    private lateinit var buttonTakePhoto: Button

    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Разрешение на камеру не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let {
                    imageViewPhoto.setImageURI(it)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewPhoto = view.findViewById(R.id.imageViewPhoto)
        editTextNote = view.findViewById(R.id.editTextNote)
        buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto)

        buttonTakePhoto.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val photoFile = createImageFile()
            val authorities = "${requireContext().packageName}.fileprovider"
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraActivityResultLauncher.launch(intent)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Ошибка создания файла для фото", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }
}
```
*Пояснение к коду:* В коде используется `ActivityResultLauncher`, который является типобезопасным способом получения результата от внешней активности. Метод `createImageFile` сохраняет снимки в `DIRECTORY_PICTURES` внутреннего хранилища приложения, что не требует дополнительных разрешений на запись во внешнюю память на новых версиях Android.

**Пример AudioFragment.kt**
```kotlin
package ru.mirea.mvolobueva.mireaproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.mirea.mvolobueva.mireaproject.R
import java.io.File

class AudioFragment : Fragment(R.layout.fragment_audio) {

    private lateinit var textViewStatus: TextView
    private lateinit var buttonRecord: Button
    private lateinit var buttonPlay: Button

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isRecording = false
    private var isPlaying = false
    private var isPermissionGranted = false

    private lateinit var audioFilePath: String

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            isPermissionGranted = granted
            if (!granted) {
                Toast.makeText(requireContext(), "Разрешение на микрофон не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewStatus = view.findViewById(R.id.textViewAudioStatus)
        buttonRecord = view.findViewById(R.id.buttonRecordAudio)
        buttonPlay = view.findViewById(R.id.buttonPlayAudio)

        audioFilePath = "${requireContext().externalCacheDir?.absolutePath}/voice_note.m4a"

        checkAudioPermission()
        updateUi()

        buttonRecord.setOnClickListener {
            if (!isPermissionGranted) {
                checkAudioPermission()
                Toast.makeText(requireContext(), "Сначала выдайте разрешение на микрофон", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
            updateUi()
        }

        buttonPlay.setOnClickListener {
            if (isPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
            updateUi()
        }
    }

    private fun checkAudioPermission() {
        isPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        try {
            val file = File(audioFilePath)
            if (file.exists()) {
                file.delete()
            }

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(requireContext())
            } else {
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }

            isRecording = true
            textViewStatus.text = "Идёт запись голосовой заметки"
            Toast.makeText(requireContext(), "Запись началась", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            textViewStatus.text = "Ошибка записи"
            Toast.makeText(requireContext(), "Ошибка записи: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка остановки записи: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            recorder = null
            isRecording = false
        }

        val file = File(audioFilePath)
        textViewStatus.text = "Запись сохранена, размер: ${file.length()} байт"
        Toast.makeText(requireContext(), "Запись остановлена", Toast.LENGTH_SHORT).show()
    }

    private fun startPlaying() {
        val file = File(audioFilePath)
        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(requireContext(), "Сначала запишите голосовую заметку", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopPlaying()
                    updateUi()
                }
            }

            isPlaying = true
            textViewStatus.text = "Идёт воспроизведение голосовой заметки"
            Toast.makeText(requireContext(), "Воспроизведение началось", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            textViewStatus.text = "Ошибка воспроизведения"
            Toast.makeText(requireContext(), "Ошибка воспроизведения: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopPlaying() {
        try {
            player?.stop()
        } catch (_: Exception) {
        }

        player?.release()
        player = null
        isPlaying = false
        textViewStatus.text = "Воспроизведение остановлено"
        Toast.makeText(requireContext(), "Воспроизведение остановлено", Toast.LENGTH_SHORT).show()
    }

    private fun updateUi() {
        buttonRecord.text = if (isRecording) "Остановить запись" else "Начать запись"
        buttonPlay.text = if (isPlaying) "Остановить воспроизведение" else "Воспроизвести"

        buttonPlay.isEnabled = !isRecording
        buttonRecord.isEnabled = !isPlaying
    }

    override fun onStop() {
        super.onStop()

        if (isRecording) {
            stopRecording()
        }
        if (isPlaying) {
            stopPlaying()
        }
        updateUi()
    }
}
```
*Пояснение к коду:* Особенностью реализации является проверка версии ОС (`Build.VERSION_CODES.S`), так как конструктор `MediaRecorder` претерпел изменения в API 31. Код обеспечивает корректное удаление предыдущего файла записи перед началом новой сессии, что экономит дисковое пространство.

В итоге контрольное задание было успешно выполнено: в архитектуру `MireaProject` внедрены три автономных модуля, демонстрирующие практическое применение аппаратного функционала (сенсоры, камера, микрофон) в рамках единого приложения.

---

### 5. Вывод
В ходе выполнения данной практической работы были глубоко изучены и отработаны на практике механизмы взаимодействия мобильного приложения с аппаратной составляющей современных Android-устройств. Ключевые результаты работы включают:

*   Освоение системного сервиса `SensorManager` для программного определения аппаратного состава устройства и получения потоковых данных с датчиков.
*   Реализацию модуля мониторинга акселерометра, что позволило изучить векторы ускорений и способы их визуализации в реальном времени.
*   Практическое внедрение системы безопасности Runtime Permissions, обеспечивающей корректную работу приложения в условиях строгих ограничений современных версий ОС.
*   Разработку функциональных блоков для работы с мультимедиа: захват и сохранение фотографий через интерфейс `FileProvider`, а также реализацию полноценного цикла записи и воспроизведения аудиосигнала.
*   Успешную интеграцию всех разработанных модулей в итоговое приложение `MireaProject` на базе фрагментов.

Полученные навыки работы с `SensorEventListener`, `MediaRecorder`, `MediaPlayer` и программными намерениями (`Intents`) являются фундаментальными для разработки сложных мобильных систем, требующих тесного взаимодействия с оборудованием и обеспечения высокого уровня безопасности пользовательских данных.