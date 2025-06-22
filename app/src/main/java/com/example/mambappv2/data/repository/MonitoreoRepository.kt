// MonitoreoRepository.kt
package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.MonitoreoDao
import com.example.mambappv2.data.entities.Monitoreo
import kotlinx.coroutines.flow.Flow

class MonitoreoRepository(
    private val monitoreoDao: MonitoreoDao
) {
    fun getAllMonitoreos(): Flow<List<Monitoreo>> = monitoreoDao.getAllMonitoreos()

    fun getMonitoreoById(id: Int): Flow<Monitoreo?> = monitoreoDao.getMonitoreoById(id)

    suspend fun insertMonitoreo(monitoreo: Monitoreo) = monitoreoDao.insertMonitoreo(monitoreo)

    suspend fun updateMonitoreo(monitoreo: Monitoreo) = monitoreoDao.updateMonitoreo(monitoreo)

    suspend fun deleteMonitoreo(monitoreo: Monitoreo) = monitoreoDao.deleteMonitoreo(monitoreo)
}
