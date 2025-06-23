// SimpleInputDialog.kt
package com.example.mambappv2.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mambappv2.ui.theme.*

@Composable
fun SimpleInputDialog(
    title: String,
    fields: List<Pair<String, MutableState<String>>>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (isDarkMode) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A2822).copy(alpha = 0.95f),
                                    Color(0xFF0E1414).copy(alpha = 0.98f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    Color(0xFFF8FAFB).copy(alpha = 0.98f)
                                )
                            )
                        }
                    )
            ) {
                Column(
                    modifier = Modifier.padding(28.dp)
                ) {
                    // Header moderno
                    ModernDialogHeader(title = title, isDarkMode = isDarkMode)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Campos de entrada modernos
                    fields.forEach { (label, state) ->
                        ModernInputField(
                            label = label,
                            value = state.value,
                            onValueChange = { state.value = it },
                            isDarkMode = isDarkMode
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botones de acción modernos
                    ModernDialogActions(
                        onDismiss = onDismiss,
                        onConfirm = onConfirm,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernDialogHeader(
    title: String,
    isDarkMode: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) 
                    HealthTeal.copy(alpha = 0.3f) 
                else 
                    HealthTeal.copy(alpha = 0.2f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp),
                    tint = HealthTeal
                )
            }
        }
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isDarkMode) Color.White.copy(alpha = 0.95f) else Color(0xFF1A1A1A)
            )
            Text(
                text = "Completa los campos requeridos",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun ModernInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label moderno con icono
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = when {
                    label.contains("Nombre", ignoreCase = true) -> Icons.Default.Person
                    label.contains("Apellido", ignoreCase = true) -> Icons.Default.Person
                    label.contains("DNI", ignoreCase = true) -> Icons.Default.Badge
                    label.contains("Edad", ignoreCase = true) -> Icons.Default.Cake
                    label.contains("Mutual", ignoreCase = true) -> Icons.Default.MedicalServices
                    label.contains("Provincia", ignoreCase = true) -> Icons.Default.Place
                    label.contains("Número", ignoreCase = true) -> Icons.Default.Numbers
                    label.contains("Descripción", ignoreCase = true) -> Icons.Default.Description
                    else -> Icons.Default.Edit
                },
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = if (isDarkMode) Color.White.copy(alpha = 0.7f) else Color(0xFF666666)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF333333)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo de entrada moderno
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) 
                    Color.White.copy(alpha = 0.1f) 
                else 
                    Color(0xFFF8FAFB)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { 
                    Text(
                        "Ingrese $label",
                        color = if (isDarkMode) 
                            Color.White.copy(alpha = 0.5f) 
                        else 
                            Color(0xFF999999)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isDarkMode) 
                        HealthTeal.copy(alpha = 0.7f) 
                    else 
                        HealthTeal,
                    unfocusedBorderColor = if (isDarkMode) 
                        Color.White.copy(alpha = 0.2f) 
                    else 
                        Color(0xFFE0E0E0),
                    cursorColor = HealthTeal,
                    focusedLabelColor = HealthTeal,
                    unfocusedLabelColor = if (isDarkMode) 
                        Color.White.copy(alpha = 0.6f) 
                    else 
                        Color(0xFF666666)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = !label.contains("Descripción", ignoreCase = true),
                maxLines = if (label.contains("Descripción", ignoreCase = true)) 3 else 1
            )
        }
    }
}

@Composable
private fun ModernDialogActions(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isDarkMode: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Botón Cancelar
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) 
                    Color.White.copy(alpha = 0.1f) 
                else 
                    Color(0xFFF0F0F0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancelar",
                        modifier = Modifier.size(18.dp),
                        tint = if (isDarkMode) 
                            Color.White.copy(alpha = 0.7f) 
                        else 
                            Color(0xFF666666)
                    )
                    Text(
                        "Cancelar",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (isDarkMode) 
                            Color.White.copy(alpha = 0.7f) 
                        else 
                            Color(0xFF666666)
                    )
                }
            }
        }
        
        // Botón Confirmar
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthTeal.copy(alpha = if (isDarkMode) 0.3f else 0.2f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmar",
                        modifier = Modifier.size(18.dp),
                        tint = HealthTeal
                    )
                    Text(
                        "Confirmar",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = HealthTeal
                    )
                }
            }
        }
    }
}
