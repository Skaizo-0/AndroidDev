package ru.mirea.mvolobueva.intentfilter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnOpenBrowser = findViewById<Button>(R.id.btnOpenBrowser)
        val btnShareData = findViewById<Button>(R.id.btnShareData)

        btnOpenBrowser.setOnClickListener {
            val address = Uri.parse("https://www.mirea.ru/")
            val openLinkIntent = Intent(Intent.ACTION_VIEW, address)
            startActivity(openLinkIntent)
        }

        btnShareData.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MIREA")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Волобуева Мария Александровна, группа БСБО-09-23")

            startActivity(Intent.createChooser(shareIntent, "МОИ ФИО"))
        }
    }
}