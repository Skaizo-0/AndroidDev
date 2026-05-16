package ru.mirea.mvolobueva.mireaproject.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.mirea.mvolobueva.mireaproject.MyWorker
import ru.mirea.mvolobueva.mireaproject.R
import ru.mirea.mvolobueva.mireaproject.databinding.FragmentWorkerBinding
/*
КРАТКОЕ ОПИСАНИЕ:
Фрагмент для демонстрации работы WorkManager - Android библиотеки для выполнения
фоновых задач. Позволяет запускать фоновые процессы, которые гарантированно выполнятся
даже если приложение закрыто или устройство перезагружено.

*/
class WorkerFragment : Fragment(R.layout.fragment_worker) {
    private lateinit var binding: FragmentWorkerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkerBinding.bind(view)

        binding.btnStartWork.setOnClickListener {
            //  СОЗДАНИЕ ЗАПРОСА НА ВЫПОЛНЕНИЕ РАБОТЫ
            val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
            // ЗАПУСК РАБОТЫ ЧЕРЕЗ WORK MANAGER
            WorkManager.getInstance(requireContext()).enqueue(workRequest)
        }
    }
}