package ru.mirea.mvolobueva.notebook

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    private lateinit var editTextFileName: EditText
    private lateinit var editTextQuote: EditText
    private lateinit var buttonSaveQuote: Button
    private lateinit var buttonLoadQuote: Button

    companion object {
        private const val TAG = "Notebook"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextFileName = findViewById(R.id.editTextFileName)
        editTextQuote = findViewById(R.id.editTextQuote)
        buttonSaveQuote = findViewById(R.id.buttonSaveQuote)
        buttonLoadQuote = findViewById(R.id.buttonLoadQuote)

        buttonSaveQuote.setOnClickListener {
            saveQuoteToFile()
        }

        buttonLoadQuote.setOnClickListener {
            loadQuoteFromFile()
        }

        showStorageDirectory()
    }

    private fun showStorageDirectory() {
        val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (path != null) {
            Log.d(TAG, "Директория для файлов: ${path.absolutePath}")
            // Показываем существующие файлы
            val files = path.listFiles()
            if (files != null && files.isNotEmpty()) {
                val fileNames = files.joinToString { it.name }
                Log.d(TAG, "Существующие файлы: $fileNames")
            }
        }
    }

    private fun saveQuoteToFile() {
        val fileName = editTextFileName.text.toString().trim()
        val quote = editTextQuote.text.toString()

        if (fileName.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show()
            return
        }

        if (quote.isEmpty()) {
            Toast.makeText(this, "Введите текст для сохранения", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (path == null) {
                Toast.makeText(this, "Не удалось получить доступ к директории", Toast.LENGTH_SHORT).show()
                return
            }

            if (!path.exists()) {
                path.mkdirs()
            }

            val file = File(path, fileName)

            // Сохраняем файл с явной кодировкой
            file.writeText(quote, StandardCharsets.UTF_8)

            Log.d(TAG, "Файл сохранён: ${file.absolutePath}")
            Log.d(TAG, "Размер файла: ${file.length()} байт")
            Log.d(TAG, "Содержимое файла: '$quote'")

            Toast.makeText(this, "Файл сохранён: $fileName (${quote.length} символов)", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения файла", e)
            Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadQuoteFromFile() {
        val fileName = editTextFileName.text.toString().trim()

        if (fileName.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (path == null) {
                Toast.makeText(this, "Не удалось получить доступ к директории", Toast.LENGTH_SHORT).show()
                return
            }

            val file = File(path, fileName)

            if (!file.exists()) {
                Log.w(TAG, "Файл не найден: ${file.absolutePath}")
                Toast.makeText(this, "Файл '$fileName' не найден", Toast.LENGTH_LONG).show()
                return
            }

            // Читаем файл
            val content = file.readText(StandardCharsets.UTF_8)

            Log.d(TAG, "Файл загружен: ${file.absolutePath}")
            Log.d(TAG, "Размер содержимого: ${content.length} символов")
            Log.d(TAG, "Содержимое файла: '$content'")
            Log.d(TAG, "Пустая строка? ${content.isEmpty()}")
            Log.d(TAG, "Только пробелы? ${content.isBlank()}")

            // ОЧИЩАЕМ EditText перед установкой нового текста
            editTextQuote.setText("")

            // Устанавливаем текст
            editTextQuote.setText(content)

            // Принудительно обновляем интерфейс
            editTextQuote.invalidate()

            // Проверяем, что текст действительно установился
            val afterSet = editTextQuote.text.toString()
            Log.d(TAG, "Текст в EditText после установки: '$afterSet'")

            if (content.isNotEmpty()) {
                Toast.makeText(this, "Файл загружен: ${content.length} символов", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Файл пуст", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка чтения файла", e)
            Toast.makeText(this, "Ошибка чтения: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}