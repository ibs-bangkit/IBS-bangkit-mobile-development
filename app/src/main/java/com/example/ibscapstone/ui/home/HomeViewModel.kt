package com.example.ibscapstone.ui.home

import androidx.lifecycle.ViewModel
import com.example.ibscapstone.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel()