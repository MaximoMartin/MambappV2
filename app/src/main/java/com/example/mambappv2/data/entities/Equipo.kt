package com.example.mambappv2.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Equipo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numero: String,
    val descripcion: String = ""
)
