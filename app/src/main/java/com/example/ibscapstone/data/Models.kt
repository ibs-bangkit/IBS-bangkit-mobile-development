package com.example.ibscapstone.data

data class RegisterRequest(
    val email: String,
    val password: String
)

data class RegisterResponse(
    val error: Boolean,
    val message: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val token : String
)

data class PredictionResponse(
    val status: String,
    val message: String,
    val data: PredictionData
)

data class PredictionData(
    val id: String,
    val result: String,
    val explanation: String,
    val suggestion: String,
    val confidenceScore: Double,
    val createdAt: String
)
