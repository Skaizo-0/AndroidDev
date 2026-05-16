package ru.mirea.mvolobueva.mireaproject

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d("MireaProjectWorker", "Фоновая задача началась")
        try {
            TimeUnit.SECONDS.sleep(5)
        } catch (e: Exception) {
            return Result.failure()
        }
        Log.d("MireaProjectWorker", "Фоновая задача успешно завершена")
        return Result.success()
    }
}