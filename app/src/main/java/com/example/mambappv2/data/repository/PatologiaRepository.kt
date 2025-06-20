package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.PatologiaDao
import com.example.mambappv2.data.entities.Patologia
import kotlinx.coroutines.flow.Flow

class PatologiaRepository(
    private val patologiaDao: PatologiaDao
) {
    fun getAllPatologias(): Flow<List<Patologia>> = patologiaDao.getAllPatologias()

    suspend fun insertPatologia(patologia: Patologia) = patologiaDao.insertPatologia(patologia)

    suspend fun updatePatologia(patologia: Patologia) = patologiaDao.updatePatologia(patologia)

    suspend fun deletePatologia(patologia: Patologia) = patologiaDao.deletePatologia(patologia)
}
