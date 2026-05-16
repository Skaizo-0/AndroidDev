package ru.mirea.mvolobueva.mireaproject.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.mirea.mvolobueva.mireaproject.R
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FilesFragment : Fragment(R.layout.fragment_files) {

    private lateinit var textViewNotes: TextView
    private lateinit var fabAddNote: FloatingActionButton

    private val fileName = "notes.txt"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewNotes = view.findViewById(R.id.textViewNotes)
        fabAddNote = view.findViewById(R.id.fabAddNote)

        loadNotesFromFile()

        fabAddNote.setOnClickListener {
            showAddNoteDialog()
        }
    }

    private fun showAddNoteDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Введите новую заметку"

        AlertDialog.Builder(requireContext())
            .setTitle("Новая запись")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val noteText = editText.text.toString().trim()

                if (noteText.isNotEmpty()) {
                    saveNoteToFile(noteText)
                    loadNotesFromFile()
                } else {
                    Toast.makeText(requireContext(), "Пустую запись сохранять нельзя", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveNoteToFile(note: String) {
        try {
            val outputStream: FileOutputStream =
                requireActivity().openFileOutput(fileName, Context.MODE_APPEND)

            outputStream.write((note + "\n").toByteArray())
            outputStream.close()

            Toast.makeText(requireContext(), "Запись сохранена", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка записи файла", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNotesFromFile() {
        var fileInputStream: FileInputStream? = null

        try {
            fileInputStream = requireActivity().openFileInput(fileName)
            val bytes = ByteArray(fileInputStream.available())
            fileInputStream.read(bytes)
            val text = String(bytes)

            textViewNotes.text = if (text.isBlank()) {
                "Пока нет сохранённых записей"
            } else {
                text
            }
        } catch (e: IOException) {
            textViewNotes.text = "Пока нет сохранённых записей"
        } finally {
            try {
                fileInputStream?.close()
            } catch (_: IOException) {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.fab
        )?.hide()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.fab
        )?.show()
    }
}