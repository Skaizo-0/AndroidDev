package ru.mirea.mvolobueva.mireaproject.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ru.mirea.mvolobueva.mireaproject.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var imageViewPhoto: ImageView
    private lateinit var editTextNote: EditText
    private lateinit var buttonTakePhoto: Button

    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Разрешение на камеру не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let {
                    imageViewPhoto.setImageURI(it)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewPhoto = view.findViewById(R.id.imageViewPhoto)
        editTextNote = view.findViewById(R.id.editTextNote)
        buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto)

        buttonTakePhoto.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val photoFile = createImageFile()
            val authorities = "${requireContext().packageName}.fileprovider"
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraActivityResultLauncher.launch(intent)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Ошибка создания файла для фото", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }
}