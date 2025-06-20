package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Medico
import com.example.mambappv2.data.repository.MedicoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicoViewModel(
    private val repository: MedicoRepository
) : ViewModel() {

    val medicos: StateFlow<List<Medico>> = repository.getAllMedicos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMedico(nombre: String, apellido: String) {
        viewModelScope.launch {
            val medico = Medico(nombre = nombre, apellido = apellido)
            repository.insertMedico(medico)
        }
    }

    fun updateMedico(medico: Medico) {
        viewModelScope.launch {
            repository.updateMedico(medico)
        }
    }

    fun deleteMedico(medico: Medico) {
        viewModelScope.launch {
            repository.deleteMedico(medico)
        }
    }
}
