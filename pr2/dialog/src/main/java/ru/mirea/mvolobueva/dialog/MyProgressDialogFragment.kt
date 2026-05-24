package ru.mirea.mvolobueva.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment

class MyProgressDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Загрузка данных...")
        builder.setMessage("Пожалуйста, подождите")
        val progressBar = ProgressBar(requireContext())
        progressBar.setPadding(0, 30, 0, 30)
        builder.setView(progressBar)

        return builder.create()
    }
}