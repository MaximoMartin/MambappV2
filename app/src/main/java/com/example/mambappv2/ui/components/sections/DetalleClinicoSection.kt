package com.example.mambappv2.ui.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mambappv2.ui.components.SectionHeader

@Composable
fun DetalleClinicoSection(
    anestesia: String,
    onAnestesiaChange: (String) -> Unit,
    complicacion: Boolean,
    onComplicacionChange: (Boolean) -> Unit,
    detalleComplicacion: String,
    onDetalleComplicacionChange: (String) -> Unit,
    cambioMotor: String,
    onCambioMotorChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader("📝 Detalles Clínicos")

        OutlinedTextField(
            value = anestesia,
            onValueChange = onAnestesiaChange,
            label = { Text("Anestesia utilizada") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Hubo complicaciones?",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = complicacion,
                onCheckedChange = onComplicacionChange
            )
        }

        if (complicacion) {
            OutlinedTextField(
                value = detalleComplicacion,
                onValueChange = onDetalleComplicacionChange,
                label = { Text("Detalle de la complicación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        OutlinedTextField(
            value = cambioMotor,
            onValueChange = onCambioMotorChange,
            label = { Text("Cambio de motor (si corresponde)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}
