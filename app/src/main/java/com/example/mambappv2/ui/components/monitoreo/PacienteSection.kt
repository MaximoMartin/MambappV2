package com.example.mambappv2.ui.components.monitoreo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Paciente

@Composable
fun PacienteSection(
    dniPaciente: String,
    onDniChange: (String) -> Unit,
    paciente: Paciente?,
    onAgregarClick: () -> Unit,
    onEditarClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo DNI moderno
        ModernDniField(
            dni = dniPaciente,
            onDniChange = onDniChange
        )

        // Información del paciente
        ModernPatientInfo(
            paciente = paciente
        )

        // Botones de acción modernos
        ModernPatientActions(
            paciente = paciente,
            onAgregarClick = onAgregarClick,
            onEditarClick = onEditarClick
        )
    }
}

@Composable
private fun ModernDniField(
    dni: String,
    onDniChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Badge,
                contentDescription = "DNI",
                modifier = Modifier.size(16.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = "DNI del Paciente *",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            OutlinedTextField(
                value = dni,
                onValueChange = { new -> if (new.all { it.isDigit() } && new.length <= 10) onDniChange(new) },
                placeholder = { 
                    Text(
                        "Ingrese DNI del paciente",
                        color = Color.White.copy(alpha = 0.6f)
                    ) 
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Paciente",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun ModernPatientInfo(
    paciente: Paciente?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Icon(
                    imageVector = if (paciente != null) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = "Estado",
                    modifier = Modifier.size(20.dp),
                    tint = if (paciente != null) Color.Green.copy(alpha = 0.8f) else Color(0xFFFF9800).copy(alpha = 0.8f)
                )
                Text(
                    text = if (paciente != null) "Paciente Registrado" else "Paciente No Registrado",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            if (paciente != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PatientInfoRow(
                        icon = Icons.Default.Person,
                        label = "Nombre",
                        value = "${paciente.nombre} ${paciente.apellido}"
                    )
                    PatientInfoRow(
                        icon = Icons.Default.Badge,
                        label = "DNI",
                        value = paciente.dniPaciente.toString()
                    )
                    PatientInfoRow(
                        icon = Icons.Default.Cake,
                        label = "Edad",
                        value = "${paciente.edad} años"
                    )
                    PatientInfoRow(
                        icon = Icons.Default.MedicalServices,
                        label = "Mutual",
                        value = paciente.mutual.takeIf { it.isNotBlank() } ?: "Sin mutual"
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Debe agregar el paciente para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun PatientInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun ModernPatientActions(
    paciente: Paciente?,
    onAgregarClick: () -> Unit,
    onEditarClick: () -> Unit
) {
    if (paciente == null) {
        Button(
            onClick = onAgregarClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.2f),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 4.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Agregar Paciente",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onEditarClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Editar Paciente",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
