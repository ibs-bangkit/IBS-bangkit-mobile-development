package com.example.ibscapstone.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibscapstone.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    suspend fun checkSession() {
        viewModelScope.launch {
            val token = userPreferences.getToken.first()
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }
}