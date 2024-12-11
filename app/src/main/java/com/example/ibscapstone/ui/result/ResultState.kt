package com.example.ibscapstone.ui.result

import com.example.ibscapstone.data.PredictionData

sealed class ResultState {
    object Loading : ResultState()
    data class Success(val data: PredictionData) : ResultState()
    data class Error(val message: String) : ResultState()
}