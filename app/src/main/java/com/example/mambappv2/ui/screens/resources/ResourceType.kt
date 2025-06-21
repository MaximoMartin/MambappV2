package com.example.mambappv2.ui.screens.resources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ResourceType(val label: String, val icon: ImageVector) {
    object Paciente : ResourceType("Pacientes", Icons.Default.Person)
    object Medico : ResourceType("Médicos", Icons.Default.MedicalServices)
    object Tecnico : ResourceType("Técnicos", Icons.Default.Build)
    object Solicitante : ResourceType("Solicitantes", Icons.Default.AccountBox)
    object Lugar : ResourceType("Lugares", Icons.Default.Place)
    object Patologia : ResourceType("Patologías", Icons.Default.MonitorHeart)
    object Equipo : ResourceType("Equipos", Icons.Default.DevicesOther)
}
