package com.example.mambappv2.data.sync

import android.content.Context
import android.util.Log
import com.example.mambappv2.data.database.AppDatabase
import com.example.mambappv2.data.entities.*
import com.example.mambappv2.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncManager(
    private val context: Context,
    private val database: AppDatabase,
    private val googleSheetsService: GoogleSheetsService,
    private val monitoreoRepository: MonitoreoRepository
) {
    companion object {
        private const val TAG = "SyncManager"
        private const val SPREADSHEET_ID_KEY = "spreadsheet_id"
    }
    
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Long>(0L)
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()
    
    private var syncJob: Job? = null
    
    /**
     * Estado de sincronización
     */
    sealed class SyncStatus {
        object Idle : SyncStatus()
        object InProgress : SyncStatus()
        object Success : SyncStatus()
        data class Error(val message: String) : SyncStatus()
    }
    
    /**
     * Configuración inicial de la sincronización
     */
    suspend fun setupSync(spreadsheetId: String) {
        try {
            // Verificar que la hoja sea accesible
            val sheetInfo = googleSheetsService.getSheetInfo(spreadsheetId)
            
            // Guardar configuración
            val sharedPrefs = context.getSharedPreferences("sync_config", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString(SPREADSHEET_ID_KEY, spreadsheetId).apply()
            
            // Crear metadatos iniciales
            database.syncMetadataDao().insertOrUpdateSyncMetadata(
                SyncMetadata(
                    tableName = "monitoreos",
                    lastSyncTimestamp = System.currentTimeMillis(),
                    sheetId = spreadsheetId,
                    sheetRange = "Monitoreos!A:T", // 19 columnas como mencionaste
                    lastKnownRowCount = 0,
                    syncStatus = "CONFIGURED"
                )
            )
            
            Log.d(TAG, "Sincronización configurada exitosamente para: ${sheetInfo.properties.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error configurando sincronización", e)
            throw SyncException("Error en configuración inicial: ${e.message}", e)
        }
    }
    
    /**
     * Sincronización completa bidireccional
     */
    suspend fun performFullSync(): Result<String> {
        return try {
            _syncStatus.value = SyncStatus.InProgress
            
            val spreadsheetId = getConfiguredSpreadsheetId()
                ?: throw SyncException("No hay spreadsheet configurado")
            
            // 1. Descargar datos de Google Sheets
            val sheetData = downloadFromSheets(spreadsheetId)
            
            // 2. Sincronizar con Room (detectar cambios locales)
            val localChanges = detectLocalChanges()
            
            // 3. Resolver conflictos y mergear datos
            val mergedData = mergeData(sheetData, localChanges)
            
            // 4. Subir cambios a Google Sheets
            uploadToSheets(spreadsheetId, mergedData)
            
            // 5. Actualizar base de datos local
            updateLocalDatabase(mergedData)
            
            // 6. Actualizar metadatos
            updateSyncMetadata("monitoreos", System.currentTimeMillis(), "COMPLETED")
            
            _syncStatus.value = SyncStatus.Success
            _lastSyncTime.value = System.currentTimeMillis()
            
            Result.success("Sincronización completada exitosamente")
            
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error(e.message ?: "Error desconocido")
            Log.e(TAG, "Error en sincronización completa", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sincronización rápida (solo cambios recientes)
     */
    suspend fun performQuickSync(): Result<String> {
        return try {
            _syncStatus.value = SyncStatus.InProgress
            
            val spreadsheetId = getConfiguredSpreadsheetId()
                ?: throw SyncException("No hay spreadsheet configurado")
            
            val metadata = database.syncMetadataDao().getSyncMetadata("monitoreos")
                ?: throw SyncException("No hay metadatos de sincronización")
            
            // Solo sincronizar cambios desde la última sincronización
            val recentChanges = getRecentLocalChanges(metadata.lastSyncTimestamp)
            
            if (recentChanges.isNotEmpty()) {
                // Subir solo los cambios recientes
                appendToSheets(spreadsheetId, recentChanges)
                updateSyncMetadata("monitoreos", System.currentTimeMillis(), "COMPLETED")
            }
            
            _syncStatus.value = SyncStatus.Success
            _lastSyncTime.value = System.currentTimeMillis()
            
            Result.success("Sincronización rápida completada")
            
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error(e.message ?: "Error desconocido")
            Log.e(TAG, "Error en sincronización rápida", e)
            Result.failure(e)
        }
    }
    
    /**
     * Iniciar sincronización automática en background
     */
    fun startAutoSync(intervalMinutes: Long = 15) {
        syncJob?.cancel()
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    performQuickSync()
                    delay(intervalMinutes * 60 * 1000) // Convertir a milisegundos
                } catch (e: Exception) {
                    Log.e(TAG, "Error en sincronización automática", e)
                    delay(5 * 60 * 1000) // Esperar 5 minutos antes de reintentar
                }
            }
        }
    }
    
    /**
     * Detener sincronización automática
     */
    fun stopAutoSync() {
        syncJob?.cancel()
        syncJob = null
    }
    
    // Métodos privados auxiliares
    
    private fun getConfiguredSpreadsheetId(): String? {
        val sharedPrefs = context.getSharedPreferences("sync_config", Context.MODE_PRIVATE)
        return sharedPrefs.getString(SPREADSHEET_ID_KEY, null)
    }
    
    private suspend fun downloadFromSheets(spreadsheetId: String): List<Monitoreo> {
        val sheetData = googleSheetsService.readSheetData(spreadsheetId, "Monitoreos!A2:T") // Excluir headers
        return convertSheetDataToMonitoreos(sheetData)
    }
    
    private suspend fun detectLocalChanges(): List<Monitoreo> {
        // Implementar detección de cambios basada en timestamps
        return monitoreoRepository.getAllMonitoreos().collect() { emptyList() }
    }
    
    private fun mergeData(sheetData: List<Monitoreo>, localChanges: List<Monitoreo>): List<Monitoreo> {
        // Implementar lógica de merge inteligente
        // Por ahora, combinar ambas listas y eliminar duplicados por ID
        val merged = (sheetData + localChanges).distinctBy { it.id }
        return merged
    }
    
    private suspend fun uploadToSheets(spreadsheetId: String, data: List<Monitoreo>) {
        val sheetValues = convertMonitoreosToSheetData(data)
        googleSheetsService.clearRange(spreadsheetId, "Monitoreos!A2:T")
        googleSheetsService.writeSheetData(spreadsheetId, "Monitoreos!A2:T", sheetValues)
    }
    
    private suspend fun appendToSheets(spreadsheetId: String, data: List<Monitoreo>) {
        val sheetValues = convertMonitoreosToSheetData(data)
        googleSheetsService.appendSheetData(spreadsheetId, "Monitoreos!A:T", sheetValues)
    }
    
    private suspend fun updateLocalDatabase(data: List<Monitoreo>) {
        data.forEach { monitoreo ->
            try {
                monitoreoRepository.insertMonitoreo(monitoreo)
            } catch (e: Exception) {
                // En caso de conflicto, actualizar
                monitoreoRepository.updateMonitoreo(monitoreo)
            }
        }
    }
    
    private suspend fun updateSyncMetadata(tableName: String, timestamp: Long, status: String) {
        database.syncMetadataDao().updateSyncStatus(tableName, timestamp, status)
    }
    
    private suspend fun getRecentLocalChanges(sinceTimestamp: Long): List<Monitoreo> {
        // Implementar consulta de cambios recientes
        // Por ahora retornamos lista vacía
        return emptyList()
    }
    
    private fun convertSheetDataToMonitoreos(sheetData: List<List<Any>>): List<Monitoreo> {
        return sheetData.mapNotNull { row ->
            try {
                if (row.size >= 19) { // Verificar que tenga todas las columnas
                    Monitoreo(
                        id = row[0].toString().toIntOrNull() ?: 0,
                        nroRegistro = row[1].toString().toIntOrNull() ?: 0,
                        fechaRealizado = row[2].toString(),
                        fechaPresentado = row[3].toString().takeIf { it.isNotBlank() },
                        fechaCobrado = row[4].toString().takeIf { it.isNotBlank() },
                        dniPaciente = row[5].toString().toIntOrNull() ?: 0,
                        idMedico = row[6].toString().toIntOrNull() ?: 0,
                        idTecnico = row[7].toString().toIntOrNull() ?: 0,
                        idLugar = row[8].toString().toIntOrNull() ?: 0,
                        idPatologia = row[9].toString().toIntOrNull() ?: 0,
                        idSolicitante = row[10].toString().toIntOrNull() ?: 0,
                        idEquipo = row[11].toString().toIntOrNull(),
                        detalleAnestesia = row[12].toString(),
                        complicacion = row[13].toString().toBoolean(),
                        detalleComplicacion = row[14].toString(),
                        cambioMotor = row[15].toString(),
                        // Los snapshots y datos del paciente irían en las columnas restantes
                        medicoSnapshot = row.getOrNull(16)?.toString() ?: "",
                        tecnicoSnapshot = row.getOrNull(17)?.toString() ?: "",
                        solicitanteSnapshot = row.getOrNull(18)?.toString() ?: ""
                        // ... continuar con el resto de campos
                    )
                } else null
            } catch (e: Exception) {
                Log.w(TAG, "Error convirtiendo fila de Sheet a Monitoreo: $row", e)
                null
            }
        }
    }
    
    private fun convertMonitoreosToSheetData(monitoreos: List<Monitoreo>): List<List<Any>> {
        return monitoreos.map { monitoreo ->
            listOf(
                monitoreo.id,
                monitoreo.nroRegistro,
                monitoreo.fechaRealizado,
                monitoreo.fechaPresentado ?: "",
                monitoreo.fechaCobrado ?: "",
                monitoreo.dniPaciente,
                monitoreo.idMedico,
                monitoreo.idTecnico,
                monitoreo.idLugar,
                monitoreo.idPatologia,
                monitoreo.idSolicitante,
                monitoreo.idEquipo ?: "",
                monitoreo.detalleAnestesia,
                monitoreo.complicacion,
                monitoreo.detalleComplicacion,
                monitoreo.cambioMotor,
                monitoreo.medicoSnapshot,
                monitoreo.tecnicoSnapshot,
                monitoreo.solicitanteSnapshot
                // ... continuar con el resto de campos
            )
        }
    }
} 