package ru.mirea.mvolobueva.multiactivity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val textView = findViewById<TextView>(R.id.textViewResult)

        val receivedText = intent.getStringExtra("key")

        if (receivedText != null) {
            textView.text = receivedText
        }
    }
}