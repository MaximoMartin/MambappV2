package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Monitoreo
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoreoDao {

    @Query("SELECT * FROM monitoreos ORDER BY fechaRealizado DESC")
    fun getAllMonitoreos(): Flow<List<Monitoreo>>

    @Query("SELECT * FROM monitoreos WHERE id = :id LIMIT 1")
    suspend fun getMonitoreoById(id: Int): Monitoreo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonitoreo(monitoreo: Monitoreo)

    @Update
    suspend fun updateMonitoreo(monitoreo: Monitoreo)

    @Delete
    suspend fun deleteMonitoreo(monitoreo: Monitoreo)
}
