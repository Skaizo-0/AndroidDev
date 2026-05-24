package ru.mirea.mvolobueva.intentapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val textView = findViewById<TextView>(R.id.textViewResult)

        val receivedTime = intent.getStringExtra("time_key") ?: "Ошибка времени"

        val myNumber = 3

        val square = myNumber * myNumber

        val resultText = "КВАДРАТ ЗНАЧЕНИЯ МОЕГО НОМЕРА ПО СПИСКУ В ГРУППЕ СОСТАВЛЯЕТ $square, а текущее время $receivedTime"

        textView.text = resultText
    }
}