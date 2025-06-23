// EquipoSection.kt
package com.example.mambappv2.ui.components.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.runtime.Composable
import com.example.mambappv2.data.entities.Equipo
import com.example.mambappv2.ui.components.ModernEntityDropdown

@Composable
fun EquipoSection(
    equipos: List<Equipo>,
    selectedEquipoId: Int,
    onEquipoSelected: (Int) -> Unit,
    onAddEquipo: () -> Unit
) {
    ModernEntityDropdown(
        label = "Equipo Utilizado",
        icon = Icons.Default.Devices,
        options = equipos.map { "Nº ${it.numero} — ${it.descripcion}" },
        selectedIndex = equipos.indexOfFirst { it.id == selectedEquipoId },
        onSelect = { onEquipoSelected(equipos[it].id) },
        onAddClick = onAddEquipo
    )
}
