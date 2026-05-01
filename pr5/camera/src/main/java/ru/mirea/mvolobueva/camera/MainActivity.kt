package ru.mirea.mvolobueva.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ru.mirea.mvolobueva.camera.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
    }

    private lateinit var binding: ActivityMainBinding
    private var isWork = false
    private var imageUri: Uri? = null

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let { uri ->
                binding.imageView.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        binding.imageView.setOnClickListener {
            if (!isWork) {
                Toast.makeText(this, "Разрешения не выданы", Toast.LENGTH_SHORT).show()
                checkPermissions()
                return@setOnClickListener
            }

            val cameraIntent = android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try {
                val photoFile = createImageFile()
                val authorities = "${applicationContext.packageName}.fileprovider"
                imageUri = FileProvider.getUriForFile(this, authorities, photoFile)

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                cameraActivityResultLauncher.launch(cameraIntent)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка создания файла для фото", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() {
        val cameraPermissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        if (cameraPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
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
            Toast.makeText(this, "Разрешение на камеру не выдано", Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "IMAGE_${timeStamp}_"
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDirectory)
    }
}