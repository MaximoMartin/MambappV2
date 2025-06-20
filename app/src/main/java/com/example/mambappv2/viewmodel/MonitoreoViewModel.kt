// MonitoreoViewModel.kt
package com.example.mambappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.data.repository.MonitoreoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonitoreoViewModel(
    private val repository: MonitoreoRepository
) : ViewModel() {

    val monitoreos: StateFlow<List<Monitoreo>> = repository.getAllMonitoreos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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
