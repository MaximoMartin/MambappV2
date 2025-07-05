package com.example.mambappv2.data.sync

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class GoogleSheetsService(
    private val credentialsStream: InputStream,
    private val applicationName: String = "MambappV2"
) {
    
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()
    
    private val sheetsService: Sheets by lazy {
        val credentials = GoogleCredentials.fromStream(credentialsStream)
            .createScoped(listOf("https://www.googleapis.com/auth/spreadsheets"))
        
        Sheets.Builder(transport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName(applicationName)
            .build()
    }
    
    /**
     * Lee datos de una hoja específica
     */
    suspend fun readSheetData(
        spreadsheetId: String,
        range: String
    ): List<List<Any>> = withContext(Dispatchers.IO) {
        try {
            val response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
            
            response.getValues() ?: emptyList()
        } catch (e: Exception) {
            throw SyncException("Error al leer datos de Google Sheets: ${e.message}", e)
        }
    }
    
    /**
     * Escribe datos a una hoja específica
     */
    suspend fun writeSheetData(
        spreadsheetId: String,
        range: String,
        values: List<List<Any>>
    ): UpdateValuesResponse = withContext(Dispatchers.IO) {
        try {
            val body = ValueRange().setValues(values)
            
            sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute()
        } catch (e: Exception) {
            throw SyncException("Error al escribir datos en Google Sheets: ${e.message}", e)
        }
    }
    
    /**
     * Agrega nuevas filas al final de la hoja
     */
    suspend fun appendSheetData(
        spreadsheetId: String,
        range: String,
        values: List<List<Any>>
    ): AppendValuesResponse = withContext(Dispatchers.IO) {
        try {
            val body = ValueRange().setValues(values)
            
            sheetsService.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .execute()
        } catch (e: Exception) {
            throw SyncException("Error al agregar datos en Google Sheets: ${e.message}", e)
        }
    }
    
    /**
     * Obtiene información básica de la hoja
     */
    suspend fun getSheetInfo(
        spreadsheetId: String
    ): Spreadsheet = withContext(Dispatchers.IO) {
        try {
            sheetsService.spreadsheets()
                .get(spreadsheetId)
                .execute()
        } catch (e: Exception) {
            throw SyncException("Error al obtener información de la hoja: ${e.message}", e)
        }
    }
    
    /**
     * Limpia un rango específico
     */
    suspend fun clearRange(
        spreadsheetId: String,
        range: String
    ): ClearValuesResponse = withContext(Dispatchers.IO) {
        try {
            val requestBody = ClearValuesRequest()
            
            sheetsService.spreadsheets().values()
                .clear(spreadsheetId, range, requestBody)
                .execute()
        } catch (e: Exception) {
            throw SyncException("Error al limpiar rango en Google Sheets: ${e.message}", e)
        }
    }
}

class SyncException(message: String, cause: Throwable? = null) : Exception(message, cause) 