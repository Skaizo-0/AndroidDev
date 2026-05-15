package ru.mirea.mvolobueva.timeservice

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var textViewDate: TextView
    private lateinit var textViewTime: TextView
    private lateinit var buttonLoadTime: Button

    private val host = "time.nist.gov"
    private val port = 13

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewDate = findViewById(R.id.textViewDate)
        textViewTime = findViewById(R.id.textViewTime)
        buttonLoadTime = findViewById(R.id.buttonLoadTime)

        buttonLoadTime.setOnClickListener {
            loadTimeFromServer()
        }
    }

    private fun loadTimeFromServer() {
        textViewDate.text = "Дата: загружается..."
        textViewTime.text = "Время: загружается..."

        Thread {
            try {
                val socket = Socket(host, port)
                val reader = SocketUtils.getReader(socket)

                reader.readLine()
                val secondLine = reader.readLine()

                socket.close()

                runOnUiThread {
                    if (secondLine.isNullOrBlank()) {
                        Toast.makeText(this, "Не удалось получить данные", Toast.LENGTH_SHORT).show()
                        textViewDate.text = "Дата: --"
                        textViewTime.text = "Время: --"
                    } else {
                        parseAndShowTime(secondLine)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
                    textViewDate.text = "Дата: --"
                    textViewTime.text = "Время: --"
                }
            }
        }.start()
    }

    private fun parseAndShowTime(response: String) {
        try {
            val parts = response.trim().split(Regex("\\s+"))

            if (parts.size >= 3) {
                val utcDate = parts[1]
                val utcTime = parts[2]

                val inputFormatter = java.text.SimpleDateFormat("yy-MM-dd HH:mm:ss", java.util.Locale.US)
                inputFormatter.timeZone = java.util.TimeZone.getTimeZone("UTC")

                val outputDateFormatter = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale("ru"))
                val outputTimeFormatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale("ru"))
                val outputZone = java.util.TimeZone.getTimeZone("Europe/Moscow")

                outputDateFormatter.timeZone = outputZone
                outputTimeFormatter.timeZone = outputZone

                val date = inputFormatter.parse("$utcDate $utcTime")

                if (date != null) {
                    textViewDate.text = "Дата: ${outputDateFormatter.format(date)}"
                    textViewTime.text = "Время: ${outputTimeFormatter.format(date)} (GMT+3)"
                } else {
                    textViewDate.text = "Дата: не распознана"
                    textViewTime.text = "Время: не распознано"
                }
            } else {
                textViewDate.text = "Дата: не распознана"
                textViewTime.text = "Время: не распознано"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            textViewDate.text = "Дата: ошибка"
            textViewTime.text = "Время: ошибка"
        }
    }
}