package ru.mirea.mvolobueva.thread

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.mirea.mvolobueva.thread.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener {

            Thread {
                try {
                    val pairsStr = binding.editTextPairs.text.toString()
                    val daysStr = binding.editTextDays.text.toString()

                    if (pairsStr.isNotEmpty() && daysStr.isNotEmpty()) {
                        val pairs = pairsStr.toFloat()
                        val days = daysStr.toFloat()

                        Thread.sleep(2000)

                        val result = pairs / days

                        runOnUiThread {
                            binding.textViewResult.text = "Среднее кол-во пар в день: $result"
                        }

                        Log.d("ThreadProject", "Вычисления в фоне завершены. Результат: $result")
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ThreadProject", "Ошибка в потоке: ${e.message}")
                }
            }.start()
        }
    }
}