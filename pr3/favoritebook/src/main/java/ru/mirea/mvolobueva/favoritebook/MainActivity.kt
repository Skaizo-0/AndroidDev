package ru.mirea.mvolobueva.favoritebook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var textViewUserBook: TextView

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val userBook = data?.getStringExtra(ShareActivity.USER_MESSAGE)
            textViewUserBook.text = "Название Вашей любимой книги: $userBook"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewUserBook = findViewById(R.id.textViewBook)
    }

    fun getInfoAboutBook(view: View) {
        val intent = Intent(this, ShareActivity::class.java)
        intent.putExtra(ShareActivity.KEY, "Анабасис")
        activityResultLauncher.launch(intent)
    }
}