package ru.mirea.mvolobueva.toastapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editTextString)
        val btnCount = findViewById<Button>(R.id.btnCount)

        btnCount.setOnClickListener {
            val text = editText.text.toString()

            val count = text.length
            
            val message = "СТУДЕНТ № 3 ГРУППА БСБО-09-23 Количество символов - $count"

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}