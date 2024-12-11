package com.example.ibscapstone.ui.register

sealed class RegisterState {
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}