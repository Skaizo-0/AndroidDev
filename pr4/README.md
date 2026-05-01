# Отчет по практической работе №4
## Дисциплина: Разработка мобильных приложений

**Выполнил:** Студент группы БСБО-09-23  
**ФИО:** Волобуева Мария Александровна
**Номер по списку:** 3  

---

## 1. Цель работы
Освоить современную технологию привязки представлений **ViewBinding**. Изучить основные способы реализации асинхронности в Android: работу с потоками (`Thread`), организацию очередей сообщений (`Looper`, `Handler`), использование загрузчиков (`Loader`), создание фоновых служб (`Service`) и внедрение планировщика задач `WorkManager`. 

---

## 2. Настройка проекта
Для всех модулей в файле `build.gradle.kts` (Module :...) была активирована функция **ViewBinding**:

```kotlin
android {
    ...
    buildFeatures {
        viewBinding = true
    }
}
```

---

## 3. Выполнение учебных модулей (Проект `Lesson4`)

### 3.1 Модуль `thread` (Работа с базовыми потоками)
**Задание:** Выполнить расчет среднего количества пар в отдельном потоке.

**Листинг `MainActivity.kt`:**
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener {
            Thread {
                val pairs = binding.editTextPairs.text.toString().toFloatOrNull() ?: 0f
                val days = binding.editTextDays.text.toString().toFloatOrNull() ?: 1f
                
                // Имитация длительной работы
                Thread.sleep(2000)
                val result = pairs / days

                // Возврат в UI-поток для обновления экрана
                runOnUiThread {
                    binding.textViewResult.text = "Результат: $result пар в день"
                }
            }.start()
        }
    }
}
```

---

### 3.2 Модуль `data_thread` (runOnUiThread, post, postDelayed)
**Задание:** Изучить последовательность выполнения задач при использовании различных методов обновления UI.

**Листинг логики переключения текстов:**
```kotlin
val t = Thread {
    TimeUnit.SECONDS.sleep(2)
    runOnUiThread(runn1) // Выполнится сразу (runn1)
    
    TimeUnit.SECONDS.sleep(1)
    binding.tvInfo.postDelayed(runn3, 2000) // Выполнится через 2 секунды (runn3)
    binding.tvInfo.post(runn2) // Выполнится в порядке очереди (runn2)
}
t.start()
```
*Вывод:* Последовательность появления текста на экране: `runn1` -> `runn2` -> `runn3`.

---

### 3.3 Модуль `looper` (Поток с очередью сообщений)
**Задание:** Создать фоновый поток, обрабатывающий сообщения с задержкой, равной возрасту студента.

**Листинг `MyLooper.kt`:**
```kotlin
class MyLooper(private val mainHandler: Handler) : Thread() {
    lateinit var mHandler: Handler

    override fun run() {
        Looper.prepare()
        mHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                val age = msg.data.getInt("AGE")
                val prof = msg.data.getString("PROFESSION")
                
                Thread.sleep((age * 1000).toLong()) // Сон на количество лет
                
                val resMsg = Message().apply {
                    data = Bundle().apply { putString("result", "Профессия: $prof") }
                }
                mainHandler.sendMessage(resMsg)
            }
        }
        Looper.loop()
    }
}
```

---

### 3.4 Модуль `CryptoLoader` (Асинхронная дешифровка)
**Задание:** Реализовать шифрование AES в главном потоке и расшифровку в `Loader`.

**Листинг `MyLoader.kt`:**
```kotlin
class MyLoader(context: Context, args: Bundle?) : AsyncTaskLoader<String>(context) {
    private var decryptedText: String? = null

    init {
        val cryptText = args?.getByteArray("word")
        val keyBytes = args?.getByteArray("key")
        if (cryptText != null && keyBytes != null) {
            val key = SecretKeySpec(keyBytes, "AES")
            decryptedText = decrypt(cryptText, key)
        }
    }

    override fun loadInBackground(): String? {
        SystemClock.sleep(5000) // Имитация тяжелых вычислений
        return decryptedText
    }

    private fun decrypt(cipherText: ByteArray, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(cipherText))
    }
}
```

---

### 3.5 Модуль `ServiceApp` (Музыкальный плеер)
**Задание:** Создать Foreground Service для проигрывания музыки в фоне (адаптация под Android 14).

**Листинг `AndroidManifest.xml` (Разрешения):**
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<service 
    android:name=".PlayerService"
    android:foregroundServiceType="mediaPlayback" />
```

**Листинг `PlayerService.kt` (Метод onCreate):**
```kotlin
override fun onCreate() {
    super.onCreate()
    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Музыкальный плеер")
        .setContentText("Играет: Свой трек.mp3")
        .setSmallIcon(android.R.drawable.ic_media_play)

    // Требование Android 14: передача типа сервиса в startForeground
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        startForeground(1, builder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
    }
    
    mediaPlayer = MediaPlayer.create(this, R.raw.music)
}
```

---

### 3.6 Модуль `work_manager` (Гарантированные задачи)
**Задание:** Настроить Worker, который запускается только при наличии интернета и зарядки.

**Листинг `MainActivity.kt`:**
```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresCharging(true)
    .build()

val workRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
    .setConstraints(constraints)
    .build()

WorkManager.getInstance(this).enqueue(workRequest)
```

---

## 4. Контрольное задание (`MireaProject`)
**Задание:** Интегрировать фоновую задачу во фрагмент проекта `MireaProject`.

1. Создан фрагмент `WorkerFragment` с кнопкой запуска.
2. Реализован `MyWorker.kt`, который выполняет полезную работу (имитация 5 секунд) в фоновом режиме.
3. Добавлена навигация:
   * **`mobile_navigation.xml`**: добавлен фрагмент `nav_worker`.
   * **`MainActivity.kt`**: ID добавлен в `AppBarConfiguration`.
   * **`activity_main_drawer.xml`**: добавлен пункт меню.

---

## 5. Вывод
В ходе работы была успешно внедрена технология **ViewBinding**, что исключило ошибки `NullPointerException` при обращении к UI и сделало код чище. Были изучены и реализованы на практике все основные подходы к асинхронности:
* **Threads** — для простых параллельных вычислений.
* **Looper/Handler** — для управления очередями задач.
* **Services** — для работы в фоне после закрытия интерфейса (музыка).
* **WorkManager** — для гарантированного выполнения задач при соблюдении системных условий.

Работа выполнена с соблюдением актуальных требований безопасности Android 14 (API 34) и использованием языка **Kotlin**.