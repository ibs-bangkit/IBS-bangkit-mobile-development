package com.example.ibscapstone.ui.result

import android.util.Log
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

            try {
                val result = repository.predictImage(imageFile)
                result.fold(
                    onSuccess = { response ->
                        _predictionState.value = ResultState.Success(response.data)
                    },
                    onFailure = { exception ->
                        val errorMessage = when (exception.message) {
                            "Server error. Please try again later" -> exception.message
                            "Image file is too large" -> "Please use a smaller image file"
                            "Network error. Please check your connection" -> exception.message
                            else -> "An error occurred while analyzing the image"
                        }
                        _predictionState.value = ResultState.Error(errorMessage ?: "Unknown error occurred")
                    }
                )
            } catch (e: Exception) {
                Log.e("AnalyzeImageError", "Error while analyzing image", e)
                _predictionState.value = ResultState.Error("An unexpected error occurred")
            }
        }
    }
}