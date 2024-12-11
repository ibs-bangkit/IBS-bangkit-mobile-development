package com.example.ibscapstone.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibscapstone.data.Repository
import com.example.ibscapstone.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    init {
        checkTokenStatus()
    }

    private fun checkTokenStatus() {
        viewModelScope.launch {
            userPreferences.getToken.collect { token ->
                if (!token.isNullOrBlank()) {
                    _loginState.value = LoginState.Success
                }
            }
        }
    }

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess { response ->

                if (response.token.isNotBlank()) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Login failed")
                }
            }.onFailure { exception ->
                _loginState.value = LoginState.Error(exception.message ?: "Login failed")
            }
        }
    }
}