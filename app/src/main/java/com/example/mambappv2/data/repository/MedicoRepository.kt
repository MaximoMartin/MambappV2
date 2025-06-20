package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.MedicoDao
import com.example.mambappv2.data.entities.Medico
import kotlinx.coroutines.flow.Flow

class MedicoRepository(
    private val medicoDao: MedicoDao
) {
    fun getAllMedicos(): Flow<List<Medico>> = medicoDao.getAllMedicos()

    suspend fun insertMedico(medico: Medico) = medicoDao.insertMedico(medico)

    suspend fun updateMedico(medico: Medico) = medicoDao.updateMedico(medico)

    suspend fun deleteMedico(medico: Medico) = medicoDao.deleteMedico(medico)
}
