// AppContainer.kt
package com.example.mambappv2.di

import android.content.Context
import androidx.room.Room
import com.example.mambappv2.data.database.AppDatabase
import com.example.mambappv2.data.repository.*

class AppContainer(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mambapp_db"
    ).build()

    val pacienteRepository = PacienteRepository(database.pacienteDao())
    val monitoreoRepository = MonitoreoRepository(database.monitoreoDao())
    val lugarRepository = LugarRepository(database.lugarDao())
    val medicoRepository = MedicoRepository(database.medicoDao())
    val tecnicoRepository = TecnicoRepository(database.tecnicoDao())
    val solicitanteRepository = SolicitanteRepository(database.solicitanteDao())
    val patologiaRepository = PatologiaRepository(database.patologiaDao())
}
