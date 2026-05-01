package ru.mirea.mvolobueva.audiorecord

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 200
    }

    private lateinit var btnRecord: Button
    private lateinit var btnPlay: Button

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isRecording = false
    private var isPlaying = false
    private var isWork = false

    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.btnRecord)
        btnPlay = findViewById(R.id.btnPlay)

        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        checkPermissions()
        updateButtons()

        btnRecord.setOnClickListener {
            if (!isWork) {
                checkPermissions()
                Toast.makeText(this, "Разрешение на микрофон не выдано", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
            updateButtons()
        }

        btnPlay.setOnClickListener {
            if (!isPlaying) {
                startPlaying()
            } else {
                stopPlaying()
            }
            updateButtons()
        }
    }

    private fun checkPermissions() {
        val audioPermissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (audioPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            isWork = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        if (!isWork) {
            Toast.makeText(this, "Разрешение на запись не выдано", Toast.LENGTH_LONG).show()
        }
    }

    private fun startRecording() {
        try {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }

            isRecording = true
            Toast.makeText(this, "Запись началась", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Ошибка записи", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false

        val file = java.io.File(fileName)
        Toast.makeText(
            this,
            "Запись остановлена, размер: ${file.length()} байт",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun startPlaying() {
        val file = java.io.File(fileName)
        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(this, "Аудиофайл не найден или пустой", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            player = MediaPlayer().apply {
                setDataSource(fileName)
                prepare()
                start()
                setOnCompletionListener {
                    stopPlaying()
                    updateButtons()
                }
            }

            isPlaying = true
            Toast.makeText(this, "Воспроизведение началось", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка воспроизведения: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        isPlaying = false
        Toast.makeText(this, "Воспроизведение остановлено", Toast.LENGTH_SHORT).show()
    }

    private fun updateButtons() {
        btnRecord.text = if (isRecording) "Остановить запись" else "Начать запись"
        btnPlay.text = if (isPlaying) "Остановить воспроизведение" else "Воспроизвести"

        btnPlay.isEnabled = !isRecording
        btnRecord.isEnabled = !isPlaying
    }

    override fun onStop() {
        super.onStop()

        if (isRecording) {
            stopRecording()
        }

        if (isPlaying) {
            stopPlaying()
        }
    }
}