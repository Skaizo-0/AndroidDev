package ru.mirea.mvolobueva.internalfilestorage

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editTextHistory: EditText
    private lateinit var buttonSaveFile: Button
    private lateinit var buttonReadFile: Button
    private lateinit var textViewFileContent: TextView

    private val fileName = "history_date.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextHistory = findViewById(R.id.editTextHistory)
        buttonSaveFile = findViewById(R.id.buttonSaveFile)
        buttonReadFile = findViewById(R.id.buttonReadFile)
        textViewFileContent = findViewById(R.id.textViewFileContent)

        editTextHistory.setText(
            "12 апреля 1961 года — первый полёт человека в космос. Юрий Гагарин стал первым человеком, совершившим орбитальный полёт вокруг Земли."
        )

        buttonSaveFile.setOnClickListener {
            saveTextToFile()
        }

        buttonReadFile.setOnClickListener {
            readTextFromFile()
        }
    }

    private fun saveTextToFile() {
        val text = editTextHistory.text.toString()

        try {
            val outputStream: FileOutputStream =
                openFileOutput(fileName, Context.MODE_PRIVATE)
            outputStream.write(text.toByteArray())
            outputStream.close()

            Toast.makeText(this, "Файл сохранён", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка записи файла", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readTextFromFile() {
        var fileInputStream: FileInputStream? = null

        try {
            fileInputStream = openFileInput(fileName)
            val bytes = ByteArray(fileInputStream.available())
            fileInputStream.read(bytes)
            val text = String(bytes)

            textViewFileContent.text = text
            Toast.makeText(this, "Файл прочитан", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка чтения файла", Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fileInputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}