package ru.mirea.mvolobueva.work_manager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Log.d("UploadWorker", "doWork: start")

        try {
            TimeUnit.SECONDS.sleep(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return Result.failure()
        }

        Log.d("UploadWorker", "doWork: end")
        return Result.success()
    }
}