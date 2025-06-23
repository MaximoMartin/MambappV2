package com.example.mambappv2.ui.components.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import com.example.mambappv2.data.entities.Lugar
import com.example.mambappv2.ui.components.ModernEntityDropdown

@Composable
fun LugarSection(
    lugares: List<Lugar>,
    selectedLugarId: Int,
    onLugarSelected: (Int) -> Unit,
    onAddLugar: () -> Unit
) {
    ModernEntityDropdown(
        label = "Lugar del Procedimiento",
        icon = Icons.Default.Place,
        options = lugares.map { "${it.nombre}, ${it.provincia}" },
        selectedIndex = lugares.indexOfFirst { it.id == selectedLugarId },
        onSelect = { onLugarSelected(lugares[it].id) },
        onAddClick = onAddLugar
    )
}
