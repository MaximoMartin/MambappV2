package com.example.mambappv2.ui.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


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
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo Anestesia
        ModernTextInputField(
            label = "Anestesia Utilizada",
            value = anestesia,
            onValueChange = onAnestesiaChange,
            icon = Icons.Default.MedicalServices,
            placeholder = "Tipo de anestesia empleada"
        )

        // Switch de complicaciones
        ModernSwitchField(
            label = "¿Hubo Complicaciones?",
            checked = complicacion,
            onCheckedChange = onComplicacionChange,
            icon = Icons.Default.Warning
        )

        // Campo detalle complicación (condicional)
        if (complicacion) {
            ModernTextInputField(
                label = "Detalle de la Complicación",
                value = detalleComplicacion,
                onValueChange = onDetalleComplicacionChange,
                icon = Icons.Default.ReportProblem,
                placeholder = "Describe la complicación presentada",
                maxLines = 3
            )
        }

        // Campo cambio motor
        ModernTextInputField(
            label = "Cambio de Motor",
            value = cambioMotor,
            onValueChange = onCambioMotorChange,
            icon = Icons.Default.Build,
            placeholder = "Si corresponde, detalla el cambio"
        )
    }
}

@Composable
private fun ModernTextInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String,
    maxLines: Int = 1
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label moderno
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Campo moderno
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { 
                    Text(
                        placeholder,
                        color = Color.White.copy(alpha = 0.6f)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White.copy(alpha = 0.9f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = maxLines,
                minLines = if (maxLines > 1) 2 else 1
            )
        }
    }
}

@Composable
private fun ModernSwitchField(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label moderno
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Switch moderno
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (checked) "Sí, hubo complicaciones" else "No hubo complicaciones",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}
