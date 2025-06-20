package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Solicitante
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitanteDao {

    @Query("SELECT * FROM solicitantes ORDER BY apellido ASC")
    fun getAllSolicitantes(): Flow<List<Solicitante>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolicitante(solicitante: Solicitante)

    @Update
    suspend fun updateSolicitante(solicitante: Solicitante)

    @Delete
    suspend fun deleteSolicitante(solicitante: Solicitante)
}
