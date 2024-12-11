package com.example.ibscapstone.data

import android.util.Log
import com.example.ibscapstone.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            val response = apiService.predictImage(imagePart)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        userPreferences.clearToken()
    }
}
