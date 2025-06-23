// ProfesionalesSection.kt
package com.example.mambappv2.ui.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.*
import com.example.mambappv2.ui.components.ModernEntityDropdown

@Composable
fun ProfesionalesSection(
    medicos: List<Medico>,
    selectedMedicoId: Int,
    onMedicoSelected: (Int) -> Unit,
    onAddMedico: () -> Unit,

    tecnicos: List<Tecnico>,
    selectedTecnicoId: Int,
    onTecnicoSelected: (Int) -> Unit,
    onAddTecnico: () -> Unit,

    solicitantes: List<Solicitante>,
    selectedSolicitanteId: Int,
    onSolicitanteSelected: (Int) -> Unit,
    onAddSolicitante: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModernEntityDropdown(
            label = "Médico Responsable",
            icon = Icons.Default.LocalHospital,
            options = medicos.map { "${it.nombre} ${it.apellido}" },
            selectedIndex = medicos.indexOfFirst { it.id == selectedMedicoId },
            onSelect = { onMedicoSelected(medicos[it].id) },
            onAddClick = onAddMedico
        )

        ModernEntityDropdown(
            label = "Técnico Asignado",
            icon = Icons.Default.Engineering,
            options = tecnicos.map { "${it.nombre} ${it.apellido}" },
            selectedIndex = tecnicos.indexOfFirst { it.id == selectedTecnicoId },
            onSelect = { onTecnicoSelected(tecnicos[it].id) },
            onAddClick = onAddTecnico
        )

        ModernEntityDropdown(
            label = "Médico Solicitante",
            icon = Icons.Default.PersonSearch,
            options = solicitantes.map { "${it.nombre} ${it.apellido}" },
            selectedIndex = solicitantes.indexOfFirst { it.id == selectedSolicitanteId },
            onSelect = { onSolicitanteSelected(solicitantes[it].id) },
            onAddClick = onAddSolicitante
        )
    }
}
