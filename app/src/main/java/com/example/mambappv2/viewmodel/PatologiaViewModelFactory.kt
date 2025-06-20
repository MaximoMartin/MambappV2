package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mambappv2.data.repository.PatologiaRepository

class PatologiaViewModelFactory(
    private val repository: PatologiaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatologiaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatologiaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
