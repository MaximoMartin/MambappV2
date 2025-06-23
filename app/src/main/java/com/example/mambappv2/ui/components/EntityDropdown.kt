// EntityDropdown.kt
package com.example.mambappv2.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityDropdown(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onAddClick: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    readOnly = true,
                    value = if (selectedIndex in options.indices) options[selectedIndex] else "",
                    onValueChange = {},
                    label = { Text("Seleccionar $label") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onSelect(index)
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (onAddClick != null) {
                OutlinedIconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar $label",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Nueva versión moderna para usar dentro de las secciones con gradientes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernEntityDropdown(
    label: String,
    icon: ImageVector,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onAddClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Label moderno con ícono
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown moderno
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    Box(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedIndex in options.indices) 
                                    options[selectedIndex] 
                                else 
                                    "Seleccionar $label",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedIndex in options.indices) FontWeight.Medium else FontWeight.Normal
                                ),
                                color = if (selectedIndex in options.indices) 
                                    Color.White.copy(alpha = 0.9f) 
                                else 
                                    Color.White.copy(alpha = 0.6f),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expandir",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        options.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        option,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    ) 
                                },
                                onClick = {
                                    onSelect(index)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Botón de agregar moderno
            if (onAddClick != null) {
                Card(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar $label",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
