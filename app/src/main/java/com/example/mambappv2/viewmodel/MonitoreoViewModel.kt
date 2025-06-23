// MonitoreoViewModel.kt
package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.data.repository.MonitoreoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MonitoreoViewModel(
    private val repository: MonitoreoRepository
) : ViewModel() {

    val monitoreos: StateFlow<List<Monitoreo>> = repository.getAllMonitoreos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMonitoreoById(id: Int): StateFlow<Monitoreo?> =
        repository.getMonitoreoById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Conteos de uso por entidad - Flows reactivos
    val medicoUsageCount: StateFlow<Map<Int, Int>> = monitoreos
        .map { monitoreoList ->
            monitoreoList.groupingBy { it.idMedico }.eachCount()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val tecnicoUsageCount: StateFlow<Map<Int, Int>> = monitoreos
        .map { monitoreoList ->
            monitoreoList.groupingBy { it.idTecnico }.eachCount()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val solicitanteUsageCount: StateFlow<Map<Int, Int>> = monitoreos
        .map { monitoreoList ->
            monitoreoList.groupingBy { it.idSolicitante }.eachCount()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val lugarUsageCount: StateFlow<Map<Int, Int>> = monitoreos
        .map { monitoreoList ->
            monitoreoList.groupingBy { it.idLugar }.eachCount()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val patologiaUsageCount: StateFlow<Map<Int, Int>> = monitoreos
        .map { monitoreoList ->
            monitoreoList.groupingBy { it.idPatologia }.eachCount()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val pacienteUsageCount: StateFlow<Map<Int, Int>> = monitoreos
        .map { monitoreoList ->
            monitoreoList.groupingBy { it.dniPaciente }.eachCount()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val equipoUsageCount: StateFlow<Map<Int, Int>> = monitoreos
        .map { monitoreoList ->
            monitoreoList.mapNotNull { it.idEquipo }
                .groupingBy { it }
                .eachCount()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun addMonitoreo(monitoreo: Monitoreo) {
        viewModelScope.launch {
            repository.insertMonitoreo(monitoreo)
        }
    }

    fun updateMonitoreo(monitoreo: Monitoreo) {
        viewModelScope.launch {
            repository.updateMonitoreo(monitoreo)
        }
    }

    fun deleteMonitoreo(monitoreo: Monitoreo) {
        viewModelScope.launch {
            repository.deleteMonitoreo(monitoreo)
        }
    }
}
