package com.example.ibscapstone.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.ibscapstone.network.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun register(email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(RegisterRequest(email, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.token.isNotBlank()) {
                // Save token
                userPreferences.saveToken(response.token)
                Result.success(response)
            } else {
                Result.failure(Exception("Invalid token received"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun predictImage(imageFile: File): Result<PredictionResponse> {
        return try {
            // Validate file
            if (!imageFile.exists()) {
                return Result.failure(Exception("Image file not found"))
            }

            // Compress and convert image
            val compressedImageFile = compressImage(imageFile)

            // Create multipart request
            val requestFile = compressedImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData(
                name = "image", // Changed from "file" to "image" to match API expectations
                filename = imageFile.name, // Use original filename
                body = requestFile
            )

            // Get token from preferences
            val token = userPreferences.getToken.first()

            // Make API call with error handling
            try {
                val response = apiService.predictImage(imagePart)
                if (response.status == "success") {
                    Result.success(response)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: retrofit2.HttpException) {
                Log.e("PredictImageError", "HTTP error: ${e.code()}", e)
                when (e.code()) {
                    500 -> Result.failure(Exception("Server error. Please try again later"))
                    413 -> Result.failure(Exception("Image file is too large"))
                    401 -> Result.failure(Exception("Unauthorized. Please login again"))
                    else -> Result.failure(Exception("Network error: ${e.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("PredictImageError", "Error predicting image: ${imageFile.name}", e)
            Result.failure(e)
        }
    }

    private fun compressImage(imageFile: File): File {
        try {
            // Load the bitmap with options to prevent OOM
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imageFile.path, options)

            // Calculate sample size
            var sampleSize = 1
            val maxDimension = 1024
            while (options.outWidth / sampleSize > maxDimension ||
                options.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }

            // Decode bitmap with sample size
            options.apply {
                inJustDecodeBounds = false
                inSampleSize = sampleSize
            }
            val bitmap = BitmapFactory.decodeFile(imageFile.path, options)
                ?: throw Exception("Failed to decode image")

            // Create scaled bitmap if necessary
            val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val scale = minOf(
                    maxDimension.toFloat() / bitmap.width,
                    maxDimension.toFloat() / bitmap.height
                )
                val newWidth = (bitmap.width * scale).toInt()
                val newHeight = (bitmap.height * scale).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }

            // Compress to JPEG with quality optimization
            val outputStream = ByteArrayOutputStream()
            var quality = 100
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            // Reduce quality if file is too large (> 1MB)
            while (outputStream.size() > 1024 * 1024 && quality > 50) {
                outputStream.reset()
                quality -= 10
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }

            // Write to temporary file
            val compressedFile = File.createTempFile("compressed_", ".jpg")
            compressedFile.writeBytes(outputStream.toByteArray())

            // Clean up
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }
            bitmap.recycle()
            outputStream.close()

            return compressedFile
        } catch (e: Exception) {
            Log.e("CompressImageError", "Error compressing image", e)
            throw e
        }
    }

    suspend fun logout() {
        userPreferences.clearToken()
    }
}
