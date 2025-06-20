package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Medico
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicoDao {

    @Query("SELECT * FROM medicos ORDER BY apellido ASC")
    fun getAllMedicos(): Flow<List<Medico>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedico(medico: Medico)

    @Update
    suspend fun updateMedico(medico: Medico)

    @Delete
    suspend fun deleteMedico(medico: Medico)
}
