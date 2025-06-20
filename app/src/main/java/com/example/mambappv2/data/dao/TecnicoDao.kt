package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Tecnico
import kotlinx.coroutines.flow.Flow

@Dao
interface TecnicoDao {

    @Query("SELECT * FROM tecnicos ORDER BY apellido ASC")
    fun getAllTecnicos(): Flow<List<Tecnico>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTecnico(tecnico: Tecnico)

    @Update
    suspend fun updateTecnico(tecnico: Tecnico)

    @Delete
    suspend fun deleteTecnico(tecnico: Tecnico)
}
