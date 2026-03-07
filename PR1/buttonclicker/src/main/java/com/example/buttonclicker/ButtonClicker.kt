package com.example.buttonclicker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ButtonClicker : AppCompatActivity() {

    // Объявляем переменные
    private lateinit var tvOut: TextView
    private lateinit var btnWhoAmI: Button
    private lateinit var checkBoxStatus: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button_clicker)

        // Связываем переменные с ID из XML (Глава 6)
        tvOut = findViewById(R.id.tvOut)
        btnWhoAmI = findViewById(R.id.btnWhoAmI)
        checkBoxStatus = findViewById(R.id.checkBoxStatus)

        // СПОСОБ 1 (стр. 39): Слушатель через код (для первой кнопки)
        btnWhoAmI.setOnClickListener {
            tvOut.text = "Мой номер по списку № X" // Впиши свой номер
            checkBoxStatus.isChecked = !checkBoxStatus.isChecked // Задание со стр. 40
        }
    }

    // СПОСОБ 2 (стр. 40): Атрибут android:onClick в XML (для второй кнопки)
    fun onMyButtonClick(view: View) {
        tvOut.text = "Это не я сделал"
        checkBoxStatus.isChecked = !checkBoxStatus.isChecked // Задание со стр. 40
        Toast.makeText(this, "Ещё один способ!", Toast.LENGTH_SHORT).show()
    }
}