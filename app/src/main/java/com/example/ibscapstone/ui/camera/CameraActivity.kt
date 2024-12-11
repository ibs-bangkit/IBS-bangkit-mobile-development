package com.example.ibscapstone.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.ibscapstone.databinding.ActivityCameraBinding
import com.example.ibscapstone.ui.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var currentPhotoPath: String? = null

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                startResultActivity(path)
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Create a file to store the selected image
            val photoFile = createImageFile()
            photoFile?.let { file ->
                // Copy the content from URI to our file
                copyUriToFile(uri, file)
                startResultActivity(file.absolutePath)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupClickListeners() {
        binding.buttonCamera.setOnClickListener {
            checkCameraPermission()
        }

        binding.buttonGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val photoFile = createImageFile()
        photoFile?.let { file ->
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )
            currentPhotoPath = file.absolutePath
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = getExternalFilesDir(null)
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
        } catch (ex: Exception) {
            null
        }
    }

    private fun copyUriToFile(uri: Uri, destFile: File) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startResultActivity(imagePath: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_PATH, imagePath)
        }
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}