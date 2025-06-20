package com.example.mambappv2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitoreos")
data class Monitoreo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "nroRegistro")
    val nroRegistro: Int,

    @ColumnInfo(name = "fechaRealizado")
    val fechaRealizado: String,

    @ColumnInfo(name = "fechaPresentado")
    val fechaPresentado: String? = null,

    @ColumnInfo(name = "fechaCobrado")
    val fechaCobrado: String? = null,

    @ColumnInfo(name = "dniPaciente")
    val dniPaciente: Int,

    @ColumnInfo(name = "idMedico")
    val idMedico: Int,

    @ColumnInfo(name = "idTecnico")
    val idTecnico: Int,

    @ColumnInfo(name = "idLugar")
    val idLugar: Int,

    @ColumnInfo(name = "idPatologia")
    val idPatologia: Int,

    @ColumnInfo(name = "idSolicitante")
    val idSolicitante: Int,

    @ColumnInfo(name = "detalleAnestesia")
    val detalleAnestesia: String = "",

    @ColumnInfo(name = "complicacion")
    val complicacion: Boolean = false,

    @ColumnInfo(name = "detalleComplicacion")
    val detalleComplicacion: String = "",

    @ColumnInfo(name = "cambioMotor")
    val cambioMotor: String = ""
)
