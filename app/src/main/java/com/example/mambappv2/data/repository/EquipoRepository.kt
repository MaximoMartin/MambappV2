package com.example.mambappv2.data.repository

import com.example.mambappv2.data.dao.EquipoDao
import com.example.mambappv2.data.entities.Equipo

class EquipoRepository(private val dao: EquipoDao) {
    fun getAllEquipos() = dao.getAllEquipos()
    suspend fun insertEquipo(equipo: Equipo) = dao.insertEquipo(equipo)
    suspend fun updateEquipo(equipo: Equipo) = dao.updateEquipo(equipo)
    suspend fun deleteEquipo(equipo: Equipo) = dao.deleteEquipo(equipo)
}
