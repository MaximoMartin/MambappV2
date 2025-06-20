package com.example.mambappv2.ui.components.sections

import androidx.compose.runtime.Composable
import com.example.mambappv2.data.entities.Patologia
import com.example.mambappv2.ui.components.EntityDropdown
import com.example.mambappv2.ui.components.SectionHeader

@Composable
fun PatologiaSection(
    patologias: List<Patologia>,
    selectedPatologiaId: Int,
    onPatologiaSelected: (Int) -> Unit,
    onAddPatologia: () -> Unit
) {
    SectionHeader("🧬 Patología")

    EntityDropdown(
        label = "Patología",
        options = patologias.map { it.nombre },
        selectedIndex = patologias.indexOfFirst { it.id == selectedPatologiaId },
        onSelect = { onPatologiaSelected(patologias[it].id) },
        onAddClick = onAddPatologia
    )
}
