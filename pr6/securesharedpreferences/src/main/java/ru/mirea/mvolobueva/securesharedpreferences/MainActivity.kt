package ru.mirea.mvolobueva.securesharedpreferences

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPoet: EditText
    private lateinit var editTextImage: EditText
    private lateinit var buttonSaveSecure: Button
    private lateinit var textViewSecureResult: TextView

    private lateinit var secureSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPoet = findViewById(R.id.editTextPoet)
        editTextImage = findViewById(R.id.editTextImage)
        buttonSaveSecure = findViewById(R.id.buttonSaveSecure)
        textViewSecureResult = findViewById(R.id.textViewSecureResult)

        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        secureSharedPreferences = EncryptedSharedPreferences.create(
            this,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        loadSecureData()

        buttonSaveSecure.setOnClickListener {
            saveSecureData()
        }
    }

    private fun saveSecureData() {
        val poet = editTextPoet.text.toString()
        val image = editTextImage.text.toString()

        secureSharedPreferences.edit()
            .putString("POET", poet)
            .putString("IMAGE", image)
            .apply()

        Toast.makeText(this, "Зашифрованные данные сохранены", Toast.LENGTH_SHORT).show()
        loadSecureData()
    }

    private fun loadSecureData() {
        val savedPoet = secureSharedPreferences.getString("POET", "Не указано")
        val savedImage = secureSharedPreferences.getString("IMAGE", "Не указано")

        editTextPoet.setText(savedPoet)
        editTextImage.setText(savedImage)

        textViewSecureResult.text = "Поэт: $savedPoet\nИзображение: $savedImage"
    }
}