package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.TecnicoDao
import com.example.mambappv2.data.entities.Tecnico
import kotlinx.coroutines.flow.Flow

class TecnicoRepository(
    private val tecnicoDao: TecnicoDao
) {
    fun getAllTecnicos(): Flow<List<Tecnico>> = tecnicoDao.getAllTecnicos()

    suspend fun insertTecnico(tecnico: Tecnico) = tecnicoDao.insertTecnico(tecnico)

    suspend fun updateTecnico(tecnico: Tecnico) = tecnicoDao.updateTecnico(tecnico)

    suspend fun deleteTecnico(tecnico: Tecnico) = tecnicoDao.deleteTecnico(tecnico)
}
