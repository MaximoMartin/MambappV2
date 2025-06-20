package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Lugar
import kotlinx.coroutines.flow.Flow

@Dao
interface LugarDao {

    @Query("SELECT * FROM lugares ORDER BY nombre ASC")
    fun getAllLugares(): Flow<List<Lugar>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLugar(lugar: Lugar)

    @Update
    suspend fun updateLugar(lugar: Lugar)

    @Delete
    suspend fun deleteLugar(lugar: Lugar)
}
