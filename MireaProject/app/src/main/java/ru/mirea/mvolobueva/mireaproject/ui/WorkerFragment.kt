package ru.mirea.mvolobueva.mireaproject.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.mirea.mvolobueva.mireaproject.MyWorker
import ru.mirea.mvolobueva.mireaproject.R
import ru.mirea.mvolobueva.mireaproject.databinding.FragmentWorkerBinding

class WorkerFragment : Fragment(R.layout.fragment_worker) {
    private lateinit var binding: FragmentWorkerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkerBinding.bind(view)

        binding.btnStartWork.setOnClickListener {
            val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
            WorkManager.getInstance(requireContext()).enqueue(workRequest)
        }
    }
}