package com.example.ibscapstone.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibscapstone.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    suspend fun register(email: String, password: String) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            repository.register(email, password)
                .onSuccess {
                    _registerState.value = RegisterState.Success
                }
                .onFailure {
                    _registerState.value = RegisterState.Error(it.message ?: "Registration failed")
                }
        }
    }
}