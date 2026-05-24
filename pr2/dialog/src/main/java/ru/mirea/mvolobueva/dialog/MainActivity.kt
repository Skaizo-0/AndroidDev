package ru.mirea.mvolobueva.dialog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnShowDialog).setOnClickListener {
            val dialogFragment = MyDialogFragment()
            dialogFragment.show(supportFragmentManager, "mirea_dialog")
        }

        findViewById<Button>(R.id.btnShowTime).setOnClickListener {
            val timeDialog = MyTimeDialogFragment()
            timeDialog.show(supportFragmentManager, "time_dialog")
        }

        findViewById<Button>(R.id.btnShowDate).setOnClickListener {
            val dateDialog = MyDateDialogFragment()
            dateDialog.show(supportFragmentManager, "date_dialog")
        }

        val btnProgress = findViewById<Button>(R.id.btnShowProgress)
        btnProgress.setOnClickListener {
            val progressDialog = MyProgressDialogFragment()
            progressDialog.show(supportFragmentManager, "progress_dialog")

            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
                Snackbar.make(it, "Загрузка успешно завершена!", Snackbar.LENGTH_LONG).show()
            }, 3000)
        }
    }

    fun onOkClicked() {
        Toast.makeText(applicationContext, "Вы выбрали кнопку \"Иду дальше\"!", Toast.LENGTH_LONG).show()
    }

    fun onCancelClicked() {
        Toast.makeText(applicationContext, "Вы выбрали кнопку \"Нет\"!", Toast.LENGTH_LONG).show()
    }

    fun onNeutralClicked() {
        Toast.makeText(applicationContext, "Вы выбрали кнопку \"На паузе\"!", Toast.LENGTH_LONG).show()
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val timeString = String.format("Выбрано время: %02d:%02d", hour, minute)
        Toast.makeText(this, timeString, Toast.LENGTH_LONG).show()
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val dateString = String.format("Выбрана дата: %02d.%02d.%d", day, month + 1, year)
        Toast.makeText(this, dateString, Toast.LENGTH_LONG).show()
    }
}