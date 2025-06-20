package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mambappv2.data.repository.LugarRepository

class LugarViewModelFactory(
    private val repository: LugarRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LugarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LugarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
