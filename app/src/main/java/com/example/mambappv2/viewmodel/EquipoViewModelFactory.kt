package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mambappv2.data.repository.EquipoRepository

class EquipoViewModelFactory(private val repository: EquipoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EquipoViewModel::class.java)) {
            return EquipoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
