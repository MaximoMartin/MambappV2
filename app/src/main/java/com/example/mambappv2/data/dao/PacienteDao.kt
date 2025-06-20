package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Paciente
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes ORDER BY apellido ASC")
    fun getAllPacientes(): Flow<List<Paciente>>

    @Query("SELECT * FROM pacientes WHERE dniPaciente = :dni LIMIT 1")
    suspend fun getPacienteByDni(dni: Int): Paciente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaciente(paciente: Paciente)

    @Update
    suspend fun updatePaciente(paciente: Paciente)

    @Delete
    suspend fun deletePaciente(paciente: Paciente)
}
