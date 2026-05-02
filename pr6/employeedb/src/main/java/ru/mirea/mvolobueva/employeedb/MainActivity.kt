package ru.mirea.mvolobueva.employeedb

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonInsertHeroes: Button
    private lateinit var buttonShowHeroes: Button
    private lateinit var textViewHeroes: TextView

    private lateinit var employeeDao: EmployeeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonInsertHeroes = findViewById(R.id.buttonInsertHeroes)
        buttonShowHeroes = findViewById(R.id.buttonShowHeroes)
        textViewHeroes = findViewById(R.id.textViewHeroes)

        employeeDao = App.instance.database.employeeDao()

        buttonInsertHeroes.setOnClickListener {
            insertHeroes()
        }

        buttonShowHeroes.setOnClickListener {
            showHeroes()
        }
    }

    private fun insertHeroes() {
        val hero1 = Employee(name = "Night Falcon", power = "Полет", city = "Москва")
        val hero2 = Employee(name = "Iron Shadow", power = "Невидимость", city = "Казань")
        val hero3 = Employee(name = "Storm Cat", power = "Управление молнией", city = "Сочи")

        employeeDao.insert(hero1)
        employeeDao.insert(hero2)
        employeeDao.insert(hero3)

        Toast.makeText(this, "Супергерои добавлены в базу", Toast.LENGTH_SHORT).show()
    }

    private fun showHeroes() {
        val employees = employeeDao.getAll()

        if (employees.isEmpty()) {
            textViewHeroes.text = "База данных пуста"
            return
        }

        val result = StringBuilder()
        for (employee in employees) {
            result.append("ID: ${employee.id}\n")
            result.append("Имя: ${employee.name}\n")
            result.append("Способность: ${employee.power}\n")
            result.append("Город: ${employee.city}\n\n")
        }

        textViewHeroes.text = result.toString()
    }
}