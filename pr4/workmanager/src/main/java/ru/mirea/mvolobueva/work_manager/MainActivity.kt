package ru.mirea.mvolobueva.work_manager

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.mirea.mvolobueva.work_manager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartWork.setOnClickListener {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build()

            val uploadWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueue(uploadWorkRequest)

            Toast.makeText(this, "Задача отправлена в очередь!", Toast.LENGTH_SHORT).show()
        }
    }
}