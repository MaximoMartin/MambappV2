package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Patologia
import com.example.mambappv2.data.repository.PatologiaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PatologiaViewModel(
    private val repository: PatologiaRepository
) : ViewModel() {

    val patologias: StateFlow<List<Patologia>> = repository.getAllPatologias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPatologia(nombre: String) {
        viewModelScope.launch {
            val patologia = Patologia(nombre = nombre)
            repository.insertPatologia(patologia)
        }
    }

    fun updatePatologia(patologia: Patologia) {
        viewModelScope.launch {
            repository.updatePatologia(patologia)
        }
    }

    fun deletePatologia(patologia: Patologia) {
        viewModelScope.launch {
            repository.deletePatologia(patologia)
        }
    }
}
