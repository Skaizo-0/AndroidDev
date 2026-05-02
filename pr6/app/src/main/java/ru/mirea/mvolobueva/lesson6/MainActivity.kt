package ru.mirea.mvolobueva.lesson6

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextGroup: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var editTextFavorite: EditText
    private lateinit var buttonSave: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextGroup = findViewById(R.id.editTextGroup)
        editTextNumber = findViewById(R.id.editTextNumber)
        editTextFavorite = findViewById(R.id.editTextFavorite)
        buttonSave = findViewById(R.id.buttonSave)

        sharedPreferences = getSharedPreferences("mirea_settings", Context.MODE_PRIVATE)

        loadData()

        buttonSave.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val group = editTextGroup.text.toString()
        val numberText = editTextNumber.text.toString()
        val favorite = editTextFavorite.text.toString()

        val number = if (numberText.isNotEmpty()) numberText.toInt() else 0

        val editor = sharedPreferences.edit()
        editor.putString("GROUP", group)
        editor.putInt("NUMBER", number)
        editor.putString("FAVORITE", favorite)
        editor.apply()

        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        val savedGroup = sharedPreferences.getString("GROUP", "")
        val savedNumber = sharedPreferences.getInt("NUMBER", 0)
        val savedFavorite = sharedPreferences.getString("FAVORITE", "")

        editTextGroup.setText(savedGroup)
        if (savedNumber != 0) {
            editTextNumber.setText(savedNumber.toString())
        }
        editTextFavorite.setText(savedFavorite)
    }
}