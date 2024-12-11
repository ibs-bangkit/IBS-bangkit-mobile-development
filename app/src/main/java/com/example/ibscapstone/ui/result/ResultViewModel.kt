package com.example.ibscapstone.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibscapstone.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _predictionState = MutableLiveData<ResultState>()
    val predictionState: LiveData<ResultState> = _predictionState

    fun analyzeImage(imageFile: File) {
        viewModelScope.launch {
            _predictionState.value = ResultState.Loading

            repository.predictImage(imageFile).fold(
                onSuccess = { response ->
                    _predictionState.value = ResultState.Success(response.data)
                },
                onFailure = { exception ->
                    _predictionState.value = ResultState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }
}