package com.example.mambappv2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey
    @ColumnInfo(name = "dniPaciente")
    val dniPaciente: Int,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellido")
    val apellido: String,

    @ColumnInfo(name = "edad")
    val edad: Int,

    @ColumnInfo(name = "mutual")
    val mutual: String
)
