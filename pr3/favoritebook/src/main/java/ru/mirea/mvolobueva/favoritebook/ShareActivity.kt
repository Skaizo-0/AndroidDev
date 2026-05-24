package ru.mirea.mvolobueva.favoritebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShareActivity : AppCompatActivity() {

    companion object {
        const val KEY = "book_name"
        const val USER_MESSAGE = "MESSAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val extras = intent.extras
        if (extras != null) {
            val textViewDevBook = findViewById<TextView>(R.id.textViewDevBook)
            val bookName = extras.getString(KEY)
            textViewDevBook.text = "Любимая книга разработчика – $bookName"
        }

        val editTextBook = findViewById<EditText>(R.id.editTextBook)
        val btnSendBook = findViewById<Button>(R.id.btnSendBook)

        btnSendBook.setOnClickListener {
            val text = editTextBook.text.toString()

            val data = Intent()
            data.putExtra(USER_MESSAGE, text)

            setResult(Activity.RESULT_OK, data)

            finish()
        }
    }
}