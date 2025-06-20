package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.LugarDao
import com.example.mambappv2.data.entities.Lugar
import kotlinx.coroutines.flow.Flow

class LugarRepository(
    private val lugarDao: LugarDao
) {
    fun getAllLugares(): Flow<List<Lugar>> = lugarDao.getAllLugares()

    suspend fun insertLugar(lugar: Lugar) = lugarDao.insertLugar(lugar)

    suspend fun updateLugar(lugar: Lugar) = lugarDao.updateLugar(lugar)

    suspend fun deleteLugar(lugar: Lugar) = lugarDao.deleteLugar(lugar)
}
