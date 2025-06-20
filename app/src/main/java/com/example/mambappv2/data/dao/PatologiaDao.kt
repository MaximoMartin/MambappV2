package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.Patologia
import kotlinx.coroutines.flow.Flow

@Dao
interface PatologiaDao {

    @Query("SELECT * FROM patologias ORDER BY nombre ASC")
    fun getAllPatologias(): Flow<List<Patologia>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatologia(patologia: Patologia)

    @Update
    suspend fun updatePatologia(patologia: Patologia)

    @Delete
    suspend fun deletePatologia(patologia: Patologia)
}
