package com.example.ibscapstone.ui.login

sealed class LoginState {
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}