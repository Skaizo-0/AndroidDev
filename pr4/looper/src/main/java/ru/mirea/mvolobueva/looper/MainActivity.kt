package ru.mirea.mvolobueva.looper

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.mirea.mvolobueva.looper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myLooper: MyLooper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainThreadHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val result = msg.data.getString("result")
                Log.d(MainActivity::class.java.simpleName, "Task execute. This is result: $result")
                Toast.makeText(this@MainActivity, result, Toast.LENGTH_LONG).show()
            }
        }

        myLooper = MyLooper(mainThreadHandler)
        myLooper.start()

        binding.btnSend.setOnClickListener {
            val ageStr = binding.editTextAge.text.toString()
            val profStr = binding.editTextProfession.text.toString()

            if (ageStr.isNotEmpty() && profStr.isNotEmpty()) {
                val age = ageStr.toInt()

                val msg = Message.obtain()
                val bundle = Bundle()

                bundle.putInt("AGE", age)
                bundle.putString("PROFESSION", profStr)
                msg.data = bundle

                if (::myLooper.isInitialized) {
                    myLooper.mHandler.sendMessage(msg)
                }
            } else {
                Toast.makeText(this, "Введите все данные!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}