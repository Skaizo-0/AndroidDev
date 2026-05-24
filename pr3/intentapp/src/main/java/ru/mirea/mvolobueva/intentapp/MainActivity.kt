package ru.mirea.mvolobueva.intentapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSendTime = findViewById<Button>(R.id.btnSendTime)

        btnSendTime.setOnClickListener {
            val dateInMillis = System.currentTimeMillis()
            val format = "yyyy-MM-dd HH:mm:ss"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            val dateString = sdf.format(Date(dateInMillis))

            val intent = Intent(this, SecondActivity::class.java)

            intent.putExtra("time_key", dateString)

            startActivity(intent)
        }
    }
}