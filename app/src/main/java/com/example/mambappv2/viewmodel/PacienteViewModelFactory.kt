package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mambappv2.data.repository.PacienteRepository

class PacienteViewModelFactory(
    private val repository: PacienteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PacienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PacienteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
