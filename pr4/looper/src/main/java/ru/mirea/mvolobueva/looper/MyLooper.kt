package ru.mirea.mvolobueva.looper

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

class MyLooper(private val mainHandler: Handler) : Thread() {

    lateinit var mHandler: Handler

    override fun run() {
        Log.d("MyLooper", "Фоновый поток запущен (run)")

        Looper.prepare()

        mHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {

                val data = msg.data
                val age = data.getInt("AGE", 0)
                val profession = data.getString("PROFESSION", "Неизвестно")

                Log.d("MyLooper", "Получено сообщение: Возраст=$age, Профессия=$profession")

                try {
                    Thread.sleep((age * 1000).toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                val message = Message()
                val bundle = Bundle()
                bundle.putString("result", "Через $age сек. я стал работать кем? Ответ: $profession")
                message.data = bundle

                mainHandler.sendMessage(message)
            }
        }

        Looper.loop()
    }
}