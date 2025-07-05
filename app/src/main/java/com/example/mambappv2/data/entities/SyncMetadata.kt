package com.example.mambappv2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadata(
    @PrimaryKey
    @ColumnInfo(name = "table_name")
    val tableName: String,
    
    @ColumnInfo(name = "last_sync_timestamp")
    val lastSyncTimestamp: Long,
    
    @ColumnInfo(name = "sheet_id")
    val sheetId: String,
    
    @ColumnInfo(name = "sheet_range")
    val sheetRange: String,
    
    @ColumnInfo(name = "last_known_row_count")
    val lastKnownRowCount: Int = 0,
    
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "PENDING" // PENDING, IN_PROGRESS, COMPLETED, ERROR
) 