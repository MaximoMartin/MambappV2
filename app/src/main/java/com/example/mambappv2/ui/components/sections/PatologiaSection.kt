package com.example.mambappv2.ui.components.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.runtime.Composable
import com.example.mambappv2.data.entities.Patologia
import com.example.mambappv2.ui.components.ModernEntityDropdown

@Composable
fun PatologiaSection(
    patologias: List<Patologia>,
    selectedPatologiaId: Int,
    onPatologiaSelected: (Int) -> Unit,
    onAddPatologia: () -> Unit
) {
    ModernEntityDropdown(
        label = "Patología a Monitorear",
        icon = Icons.Default.MedicalServices,
        options = patologias.map { it.nombre },
        selectedIndex = patologias.indexOfFirst { it.id == selectedPatologiaId },
        onSelect = { onPatologiaSelected(patologias[it].id) },
        onAddClick = onAddPatologia
    )
}
