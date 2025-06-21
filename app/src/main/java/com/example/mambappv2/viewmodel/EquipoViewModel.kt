package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Equipo
import com.example.mambappv2.data.repository.EquipoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EquipoViewModel(private val repository: EquipoRepository) : ViewModel() {
    val equipos: StateFlow<List<Equipo>> = repository.getAllEquipos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEquipo(numero: String, descripcion: String) {
        viewModelScope.launch {
            repository.insertEquipo(Equipo(numero = numero, descripcion = descripcion))
        }
    }

    fun updateEquipo(equipo: Equipo) {
        viewModelScope.launch { repository.updateEquipo(equipo) }
    }

    fun deleteEquipo(equipo: Equipo) {
        viewModelScope.launch { repository.deleteEquipo(equipo) }
    }
}
