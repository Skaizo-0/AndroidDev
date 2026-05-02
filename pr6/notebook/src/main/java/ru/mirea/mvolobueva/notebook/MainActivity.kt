package ru.mirea.mvolobueva.notebook

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    private lateinit var editTextFileName: EditText
    private lateinit var editTextQuote: EditText
    private lateinit var buttonSaveQuote: Button
    private lateinit var buttonLoadQuote: Button

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
    }

    private fun saveQuoteToFile() {
        val fileName = editTextFileName.text.toString().trim()
        val quote = editTextQuote.text.toString()

        if (fileName.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(path, fileName)

            val outputStream = FileOutputStream(file)
            outputStream.write(quote.toByteArray())
            outputStream.close()

            Toast.makeText(this, "Файл сохранён: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка сохранения файла", Toast.LENGTH_SHORT).show()
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
            val file = File(path, fileName)

            if (!file.exists()) {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show()
                return
            }

            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
            val reader = BufferedReader(inputStreamReader)

            val lines = mutableListOf<String>()
            var line = reader.readLine()

            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }

            reader.close()
            fileInputStream.close()

            editTextQuote.setText(lines.joinToString("\n"))

            Toast.makeText(this, "Файл загружен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка чтения файла", Toast.LENGTH_SHORT).show()
        }
    }
}