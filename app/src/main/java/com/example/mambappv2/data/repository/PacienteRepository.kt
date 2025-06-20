package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.PacienteDao
import com.example.mambappv2.data.entities.Paciente
import kotlinx.coroutines.flow.Flow

class PacienteRepository(
    private val pacienteDao: PacienteDao
) {
    fun getAllPacientes(): Flow<List<Paciente>> = pacienteDao.getAllPacientes()

    suspend fun insertPaciente(paciente: Paciente) = pacienteDao.insertPaciente(paciente)

    suspend fun updatePaciente(paciente: Paciente) = pacienteDao.updatePaciente(paciente)

    suspend fun deletePaciente(paciente: Paciente) = pacienteDao.deletePaciente(paciente)
}
