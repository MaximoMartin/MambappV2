package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Paciente
import com.example.mambappv2.data.repository.PacienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PacienteViewModel(
    private val repository: PacienteRepository
) : ViewModel() {

    val pacientes: StateFlow<List<Paciente>> = repository.getAllPacientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPaciente(dni: Int, nombre: String, apellido: String, edad: Int, mutual: String) {
        viewModelScope.launch {
            val paciente = Paciente(
                dniPaciente = dni,
                nombre = nombre,
                apellido = apellido,
                edad = edad,
                mutual = mutual
            )
            repository.insertPaciente(paciente)
        }
    }

    fun updatePaciente(paciente: Paciente) {
        viewModelScope.launch {
            repository.updatePaciente(paciente)
        }
    }

    fun deletePaciente(paciente: Paciente) {
        viewModelScope.launch {
            repository.deletePaciente(paciente)
        }
    }
}
