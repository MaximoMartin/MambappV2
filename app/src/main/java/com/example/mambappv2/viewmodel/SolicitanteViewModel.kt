package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Solicitante
import com.example.mambappv2.data.repository.SolicitanteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolicitanteViewModel(
    private val repository: SolicitanteRepository
) : ViewModel() {

    val solicitantes: StateFlow<List<Solicitante>> = repository.getAllSolicitantes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSolicitante(nombre: String, apellido: String) {
        viewModelScope.launch {
            val solicitante = Solicitante(nombre = nombre, apellido = apellido)
            repository.insertSolicitante(solicitante)
        }
    }

    fun updateSolicitante(solicitante: Solicitante) {
        viewModelScope.launch {
            repository.updateSolicitante(solicitante)
        }
    }

    fun deleteSolicitante(solicitante: Solicitante) {
        viewModelScope.launch {
            repository.deleteSolicitante(solicitante)
        }
    }
}
