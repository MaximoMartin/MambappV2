package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mambappv2.data.repository.MedicoRepository

class MedicoViewModelFactory(
    private val repository: MedicoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
