package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mambappv2.data.repository.SolicitanteRepository

class SolicitanteViewModelFactory(
    private val repository: SolicitanteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitanteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SolicitanteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
