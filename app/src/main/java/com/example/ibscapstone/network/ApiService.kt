package com.example.ibscapstone.network

import com.example.ibscapstone.data.Article
import com.example.ibscapstone.data.LoginRequest
import com.example.ibscapstone.data.LoginResponse
import com.example.ibscapstone.data.PredictionResponse
import com.example.ibscapstone.data.RegisterRequest
import com.example.ibscapstone.data.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @GET("articles")
    suspend fun getArticles(): List<Article>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @Multipart
    @POST("predict")
    suspend fun predictImage(
        @Part image: MultipartBody.Part
    ): PredictionResponse
}