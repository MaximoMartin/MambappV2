package com.example.mambappv2.ui.components.monitoreo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Paciente
import com.example.mambappv2.ui.components.SectionHeader

@Composable
fun PacienteSection(
    dniPaciente: String,
    onDniChange: (String) -> Unit,
    paciente: Paciente?,
    onAgregarClick: () -> Unit,
    onEditarClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader("👤 Paciente")

        OutlinedTextField(
            value = dniPaciente,
            onValueChange = { new -> if (new.all { it.isDigit() }) onDniChange(new) },
            label = { Text("DNI del Paciente") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (paciente != null)
                "Paciente: ${paciente.nombre} ${paciente.apellido} (${paciente.dniPaciente})"
            else
                "Paciente no registrado",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (paciente == null) {
                Button(onClick = onAgregarClick, modifier = Modifier.weight(1f)) {
                    Text("➕ Agregar")
                }
            } else {
                OutlinedButton(onClick = onEditarClick, modifier = Modifier.weight(1f)) {
                    Text("✏️ Editar")
                }
            }
        }
    }
}
