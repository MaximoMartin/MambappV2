// Monitoreo.kt
package com.example.mambappv2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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

    @ColumnInfo(name = "idEquipo")
    val idEquipo: Int? = null,

    @ColumnInfo(name = "detalleAnestesia")
    val detalleAnestesia: String = "",

    @ColumnInfo(name = "complicacion")
    val complicacion: Boolean = false,

    @ColumnInfo(name = "detalleComplicacion")
    val detalleComplicacion: String = "",

    @ColumnInfo(name = "cambioMotor")
    val cambioMotor: String = "",

    // Campos snapshot inmutables
    @ColumnInfo(name = "medicoSnapshot")
    val medicoSnapshot: String = "",

    @ColumnInfo(name = "tecnicoSnapshot")
    val tecnicoSnapshot: String = "",

    @ColumnInfo(name = "solicitanteSnapshot")
    val solicitanteSnapshot: String = "",

    @ColumnInfo(name = "lugarSnapshot")
    val lugarSnapshot: String = "",

    @ColumnInfo(name = "patologiaSnapshot")
    val patologiaSnapshot: String = "",

    @ColumnInfo(name = "equipoSnapshot")
    val equipoSnapshot: String = "",

    @ColumnInfo(name = "pacienteNombre")
    val pacienteNombre: String = "",

    @ColumnInfo(name = "pacienteApellido")
    val pacienteApellido: String = "",

    @ColumnInfo(name = "pacienteEdad")
    val pacienteEdad: Int = 0,

    @ColumnInfo(name = "pacienteMutual")
    val pacienteMutual: String = ""
) : Serializable

