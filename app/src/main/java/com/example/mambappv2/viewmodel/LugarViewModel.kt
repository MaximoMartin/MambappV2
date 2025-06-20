package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Lugar
import com.example.mambappv2.data.repository.LugarRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LugarViewModel(
    private val repository: LugarRepository
) : ViewModel() {

    val lugares: StateFlow<List<Lugar>> = repository.getAllLugares()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addLugar(nombre: String, provincia: String) {
        viewModelScope.launch {
            val lugar = Lugar(nombre = nombre, provincia = provincia)
            repository.insertLugar(lugar)
        }
    }

    fun updateLugar(lugar: Lugar) {
        viewModelScope.launch {
            repository.updateLugar(lugar)
        }
    }

    fun deleteLugar(lugar: Lugar) {
        viewModelScope.launch {
            repository.deleteLugar(lugar)
        }
    }
}
