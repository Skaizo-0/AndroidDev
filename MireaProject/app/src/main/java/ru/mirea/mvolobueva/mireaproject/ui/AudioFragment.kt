package ru.mirea.mvolobueva.mireaproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.mirea.mvolobueva.mireaproject.R
import java.io.File

class AudioFragment : Fragment(R.layout.fragment_audio) {

    private lateinit var textViewStatus: TextView
    private lateinit var buttonRecord: Button
    private lateinit var buttonPlay: Button

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isRecording = false
    private var isPlaying = false
    private var isPermissionGranted = false

    private lateinit var audioFilePath: String

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            isPermissionGranted = granted
            if (!granted) {
                Toast.makeText(requireContext(), "Разрешение на микрофон не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewStatus = view.findViewById(R.id.textViewAudioStatus)
        buttonRecord = view.findViewById(R.id.buttonRecordAudio)
        buttonPlay = view.findViewById(R.id.buttonPlayAudio)

        audioFilePath = "${requireContext().externalCacheDir?.absolutePath}/voice_note.m4a"

        checkAudioPermission()
        updateUi()

        buttonRecord.setOnClickListener {
            if (!isPermissionGranted) {
                checkAudioPermission()
                Toast.makeText(requireContext(), "Сначала выдайте разрешение на микрофон", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
            updateUi()
        }

        buttonPlay.setOnClickListener {
            if (isPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
            updateUi()
        }
    }

    private fun checkAudioPermission() {
        isPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        try {
            val file = File(audioFilePath)
            if (file.exists()) {
                file.delete()
            }

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(requireContext())
            } else {
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }

            isRecording = true
            textViewStatus.text = "Идёт запись голосовой заметки"
            Toast.makeText(requireContext(), "Запись началась", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            textViewStatus.text = "Ошибка записи"
            Toast.makeText(requireContext(), "Ошибка записи: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка остановки записи: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            recorder = null
            isRecording = false
        }

        val file = File(audioFilePath)
        textViewStatus.text = "Запись сохранена, размер: ${file.length()} байт"
        Toast.makeText(requireContext(), "Запись остановлена", Toast.LENGTH_SHORT).show()
    }

    private fun startPlaying() {
        val file = File(audioFilePath)
        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(requireContext(), "Сначала запишите голосовую заметку", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopPlaying()
                    updateUi()
                }
            }

            isPlaying = true
            textViewStatus.text = "Идёт воспроизведение голосовой заметки"
            Toast.makeText(requireContext(), "Воспроизведение началось", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            textViewStatus.text = "Ошибка воспроизведения"
            Toast.makeText(requireContext(), "Ошибка воспроизведения: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopPlaying() {
        try {
            player?.stop()
        } catch (_: Exception) {
        }

        player?.release()
        player = null
        isPlaying = false
        textViewStatus.text = "Воспроизведение остановлено"
        Toast.makeText(requireContext(), "Воспроизведение остановлено", Toast.LENGTH_SHORT).show()
    }

    private fun updateUi() {
        buttonRecord.text = if (isRecording) "Остановить запись" else "Начать запись"
        buttonPlay.text = if (isPlaying) "Остановить воспроизведение" else "Воспроизвести"

        buttonPlay.isEnabled = !isRecording
        buttonRecord.isEnabled = !isPlaying
    }

    override fun onStop() {
        super.onStop()

        if (isRecording) {
            stopRecording()
        }
        if (isPlaying) {
            stopPlaying()
        }
        updateUi()
    }
}