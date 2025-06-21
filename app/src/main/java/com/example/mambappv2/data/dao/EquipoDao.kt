package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Equipo
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Query("SELECT * FROM Equipo ORDER BY numero")
    fun getAllEquipos(): Flow<List<Equipo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: Equipo)

    @Update
    suspend fun updateEquipo(equipo: Equipo)

    @Delete
    suspend fun deleteEquipo(equipo: Equipo)
}
