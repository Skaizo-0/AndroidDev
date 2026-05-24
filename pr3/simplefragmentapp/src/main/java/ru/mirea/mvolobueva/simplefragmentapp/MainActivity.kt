package ru.mirea.mvolobueva.simplefragmentapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentContainer = findViewById<View>(R.id.fragmentContainer)

        if (fragmentContainer != null) {
            val fragmentManager: FragmentManager = supportFragmentManager

            findViewById<View>(R.id.btnFragment1).setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FirstFragment())
                    .commit()
            }

            findViewById<View>(R.id.btnFragment2).setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SecondFragment())
                    .commit()
            }
        }
    }
}