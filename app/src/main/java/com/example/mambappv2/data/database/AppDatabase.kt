//AppDatabase.kt
package com.example.mambappv2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mambappv2.data.dao.*
import com.example.mambappv2.data.entities.*

@Database(
    entities = [
        Paciente::class,
        Medico::class,
        Tecnico::class,
        Lugar::class,
        Patologia::class,
        Solicitante::class,
        Monitoreo::class,
        Equipo::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pacienteDao(): PacienteDao
    abstract fun medicoDao(): MedicoDao
    abstract fun tecnicoDao(): TecnicoDao
    abstract fun lugarDao(): LugarDao
    abstract fun patologiaDao(): PatologiaDao
    abstract fun solicitanteDao(): SolicitanteDao
    abstract fun equipoDao(): EquipoDao
    abstract fun monitoreoDao(): MonitoreoDao
}
