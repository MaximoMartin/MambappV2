package com.example.mambappv2.ui.components.sections

import androidx.compose.runtime.Composable
import com.example.mambappv2.data.entities.Lugar
import com.example.mambappv2.ui.components.EntityDropdown
import com.example.mambappv2.ui.components.SectionHeader

@Composable
fun LugarSection(
    lugares: List<Lugar>,
    selectedLugarId: Int,
    onLugarSelected: (Int) -> Unit,
    onAddLugar: () -> Unit
) {
    SectionHeader("📍 Lugar")

    EntityDropdown(
        label = "Lugar",
        options = lugares.map { "${it.nombre}, ${it.provincia}" },
        selectedIndex = lugares.indexOfFirst { it.id == selectedLugarId },
        onSelect = { onLugarSelected(lugares[it].id) },
        onAddClick = onAddLugar
    )
}
