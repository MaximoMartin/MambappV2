package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Tecnico
import com.example.mambappv2.data.repository.TecnicoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TecnicoViewModel(
    private val repository: TecnicoRepository
) : ViewModel() {

    val tecnicos: StateFlow<List<Tecnico>> = repository.getAllTecnicos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTecnico(nombre: String, apellido: String) {
        viewModelScope.launch {
            val tecnico = Tecnico(nombre = nombre, apellido = apellido)
            repository.insertTecnico(tecnico)
        }
    }

    fun updateTecnico(tecnico: Tecnico) {
        viewModelScope.launch {
            repository.updateTecnico(tecnico)
        }
    }

    fun deleteTecnico(tecnico: Tecnico) {
        viewModelScope.launch {
            repository.deleteTecnico(tecnico)
        }
    }
}
