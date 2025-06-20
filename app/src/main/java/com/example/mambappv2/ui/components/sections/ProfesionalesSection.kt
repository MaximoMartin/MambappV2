// ProfesionalesSection.kt
package com.example.mambappv2.ui.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mambappv2.data.entities.*
import com.example.mambappv2.ui.components.EntityDropdown
import com.example.mambappv2.ui.components.SectionHeader
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp

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
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("👨‍⚕️ Profesionales")

        EntityDropdown(
            label = "Médico",
            options = medicos.map { "${it.nombre} ${it.apellido}" },
            selectedIndex = medicos.indexOfFirst { it.id == selectedMedicoId },
            onSelect = { onMedicoSelected(medicos[it].id) },
            onAddClick = onAddMedico
        )

        EntityDropdown(
            label = "Técnico",
            options = tecnicos.map { "${it.nombre} ${it.apellido}" },
            selectedIndex = tecnicos.indexOfFirst { it.id == selectedTecnicoId },
            onSelect = { onTecnicoSelected(tecnicos[it].id) },
            onAddClick = onAddTecnico
        )

        EntityDropdown(
            label = "Solicitante",
            options = solicitantes.map { "${it.nombre} ${it.apellido}" },
            selectedIndex = solicitantes.indexOfFirst { it.id == selectedSolicitanteId },
            onSelect = { onSolicitanteSelected(solicitantes[it].id) },
            onAddClick = onAddSolicitante
        )
    }
}
