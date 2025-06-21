// EquipoSection.kt
package com.example.mambappv2.ui.components.sections

import androidx.compose.runtime.Composable
import com.example.mambappv2.data.entities.Equipo
import com.example.mambappv2.ui.components.EntityDropdown
import com.example.mambappv2.ui.components.SectionHeader

@Composable
fun EquipoSection(
    equipos: List<Equipo>,
    selectedEquipoId: Int,
    onEquipoSelected: (Int) -> Unit,
    onAddEquipo: () -> Unit
) {
    SectionHeader("🔧 Equipo")

    EntityDropdown(
        label = "Equipo",
        options = equipos.map { "Nº ${it.numero} - ${it.descripcion}" },
        selectedIndex = equipos.indexOfFirst { it.id == selectedEquipoId },
        onSelect = { onEquipoSelected(equipos[it].id) },
        onAddClick = onAddEquipo
    )
}
