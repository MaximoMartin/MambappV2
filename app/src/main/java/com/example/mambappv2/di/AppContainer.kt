// AppContainer.kt
package com.example.mambappv2.di

import android.content.Context
import androidx.room.Room
import com.example.mambappv2.data.database.AppDatabase
import com.example.mambappv2.data.repository.*
import com.example.mambappv2.data.sync.GoogleSheetsService
import com.example.mambappv2.data.sync.SyncManager
import java.io.InputStream

class AppContainer private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: AppContainer? = null
        
        fun getInstance(context: Context): AppContainer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppContainer(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mambapp_db"
    )
        .fallbackToDestructiveMigration() // TODO Eliminar esta linea para produccion.
        .build()

    val pacienteRepository = PacienteRepository(database.pacienteDao())
    val monitoreoRepository = MonitoreoRepository(database.monitoreoDao())
    val lugarRepository = LugarRepository(database.lugarDao())
    val medicoRepository = MedicoRepository(database.medicoDao())
    val tecnicoRepository = TecnicoRepository(database.tecnicoDao())
    val solicitanteRepository = SolicitanteRepository(database.solicitanteDao())
    val patologiaRepository = PatologiaRepository(database.patologiaDao())
    val equipoRepository by lazy { EquipoRepository(database.equipoDao()) }
    
    private val googleSheetsService: GoogleSheetsService by lazy {
        val credentialsStream = getGoogleCredentialsStream()
        GoogleSheetsService(credentialsStream)
    }
    
    val syncManager: SyncManager by lazy {
        SyncManager(
            context = context,
            database = database,
            googleSheetsService = googleSheetsService,
            monitoreoRepository = monitoreoRepository
        )
    }
    
    private fun getGoogleCredentialsStream(): InputStream {
        return context.assets.open("google_credentials.json")
    }
}
