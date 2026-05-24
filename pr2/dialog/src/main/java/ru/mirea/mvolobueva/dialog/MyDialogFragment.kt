package ru.mirea.mvolobueva.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Здравствуй МИРЭА!")
            .setMessage("Успех близок?")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton("Иду дальше") { dialog, id ->
                (activity as MainActivity).onOkClicked()
                dialog.cancel()
            }
            .setNeutralButton("На паузе") { dialog, id ->
                (activity as MainActivity).onNeutralClicked()
                dialog.cancel()
            }
            .setNegativeButton("Нет") { dialog, id ->
                (activity as MainActivity).onCancelClicked()
                dialog.cancel()
            }
        return builder.create()
    }
}