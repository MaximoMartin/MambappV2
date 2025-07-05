package com.example.mambappv2.data.dao

import androidx.room.*
import com.example.mambappv2.data.entities.SyncMetadata
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncMetadataDao {
    
    @Query("SELECT * FROM sync_metadata WHERE table_name = :tableName")
    suspend fun getSyncMetadata(tableName: String): SyncMetadata?
    
    @Query("SELECT * FROM sync_metadata")
    fun getAllSyncMetadata(): Flow<List<SyncMetadata>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSyncMetadata(syncMetadata: SyncMetadata)
    
    @Query("UPDATE sync_metadata SET last_sync_timestamp = :timestamp, sync_status = :status WHERE table_name = :tableName")
    suspend fun updateSyncStatus(tableName: String, timestamp: Long, status: String)
    
    @Query("DELETE FROM sync_metadata WHERE table_name = :tableName")
    suspend fun deleteSyncMetadata(tableName: String)
} 