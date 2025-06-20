package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.SolicitanteDao
import com.example.mambappv2.data.entities.Solicitante
import kotlinx.coroutines.flow.Flow

class SolicitanteRepository(
    private val solicitanteDao: SolicitanteDao
) {
    fun getAllSolicitantes(): Flow<List<Solicitante>> = solicitanteDao.getAllSolicitantes()

    suspend fun insertSolicitante(solicitante: Solicitante) = solicitanteDao.insertSolicitante(solicitante)

    suspend fun updateSolicitante(solicitante: Solicitante) = solicitanteDao.updateSolicitante(solicitante)

    suspend fun deleteSolicitante(solicitante: Solicitante) = solicitanteDao.deleteSolicitante(solicitante)
}
