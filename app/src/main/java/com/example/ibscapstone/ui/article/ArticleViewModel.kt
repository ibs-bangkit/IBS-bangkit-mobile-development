package com.example.ibscapstone.ui.article

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibscapstone.data.Article
import com.example.ibscapstone.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ArticleViewModel"

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _articles = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val articles: StateFlow<UiState<List<Article>>> = _articles.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized, fetching articles")
        fetchArticles()
    }

    fun fetchArticles() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting to fetch articles")
                _articles.value = UiState.Loading

                repository.getArticles()
                    .onSuccess { articles ->
                        Log.d(TAG, "Articles fetched successfully: ${articles.size} articles")
                        _articles.value = UiState.Success(articles)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to fetch articles", exception)
                        Log.e(TAG, "Error details - Message: ${exception.message}")
                        Log.e(TAG, "Error details - Cause: ${exception.cause}")
                        Log.e(TAG, "Error details - Stack trace: ${exception.stackTraceToString()}")

                        _articles.value = UiState.Error(exception.message ?: "Unknown error occurred")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in fetchArticles", e)
                Log.e(TAG, "Unexpected error details: ${e.stackTraceToString()}")
                _articles.value = UiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}