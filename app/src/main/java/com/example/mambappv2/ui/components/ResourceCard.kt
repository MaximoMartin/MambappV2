// ResourceCard.kt
package com.example.mambappv2.ui.components

import androidx.compose.animation.core.*
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
import com.example.mambappv2.ui.theme.*

@Composable
fun ResourceCard(
    title: String,
    subtitle: String? = null,
    usageCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    // Animación sutil de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "card_fade"
    )

    // Gradientes dinámicos basados en uso
    val cardGradient = when {
        usageCount == 0 -> if (isDarkMode) {
            listOf(
                Color(0xFF2A3F47).copy(alpha = 0.8f),
                Color(0xFF1E2D33).copy(alpha = 0.9f)
            )
        } else {
            listOf(
                Color(0xFFF8FAFB),
                Color(0xFFEDF2F4)
            )
        }
        usageCount < 5 -> if (isDarkMode) {
            listOf(
                MedicalTeal80.copy(alpha = 0.3f),
                Color(0xFF1A4A52).copy(alpha = 0.8f)
            )
        } else {
            listOf(
                HealthTeal.copy(alpha = 0.1f),
                Color(0xFFE8F9F6)
            )
        }
        else -> if (isDarkMode) {
            listOf(
                HealthGreen.copy(alpha = 0.3f),
                Color(0xFF1A5228).copy(alpha = 0.8f)
            )
        } else {
            listOf(
                HealthGreen.copy(alpha = 0.1f),
                Color(0xFFE8F5E8)
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkMode) 8.dp else 4.dp,
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = cardGradient,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(400f, 200f)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header con icono de estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Icono de estado basado en uso
                        Card(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    usageCount == 0 -> if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.2f)
                                    usageCount < 5 -> HealthTeal.copy(alpha = 0.3f)
                                    else -> HealthGreen.copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        usageCount == 0 -> Icons.Default.RadioButtonUnchecked
                                        usageCount < 5 -> Icons.Default.CheckCircleOutline
                                        else -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = "Estado",
                                    modifier = Modifier.size(18.dp),
                                    tint = when {
                                        usageCount == 0 -> if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Gray
                                        usageCount < 5 -> HealthTeal
                                        else -> HealthGreen
                                    }
                                )
                            }
                        }
                        
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isDarkMode) Color.White.copy(alpha = 0.95f) else Color(0xFF1A1A1A)
                            )
                            
                            if (subtitle != null) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDarkMode) Color.White.copy(alpha = 0.7f) else Color(0xFF666666)
                                )
                            }
                        }
                    }
                    
                    // Badge de uso
                    ModernUsageBadge(usageCount = usageCount, isDarkMode = isDarkMode)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información de uso detallada
                ModernUsageInfo(usageCount = usageCount, isDarkMode = isDarkMode)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botones de acción modernos
                ModernActionButtons(
                    canDelete = usageCount == 0,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
private fun ModernUsageBadge(
    usageCount: Int,
    isDarkMode: Boolean
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                usageCount == 0 -> if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.2f)
                usageCount < 5 -> HealthTeal.copy(alpha = 0.2f)
                else -> HealthGreen.copy(alpha = 0.2f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = usageCount.toString(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = when {
                    usageCount == 0 -> if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Gray
                    usageCount < 5 -> HealthTeal
                    else -> HealthGreen
                }
            )
        }
    }
}

@Composable
private fun ModernUsageInfo(
    usageCount: Int,
    isDarkMode: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Analytics,
            contentDescription = "Estadísticas",
            modifier = Modifier.size(16.dp),
            tint = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)
        )
        
        Text(
            text = when (usageCount) {
                0 -> "Sin usos registrados"
                1 -> "Usado en 1 monitoreo"
                else -> "Usado en $usageCount monitoreos"
            },
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (usageCount > 0) FontWeight.Medium else FontWeight.Normal
            ),
            color = when {
                usageCount == 0 -> if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)
                usageCount < 5 -> HealthTeal
                else -> HealthGreen
            }
        )
    }
}

@Composable
private fun ModernActionButtons(
    canDelete: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isDarkMode: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Botón Editar
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) 
                    Color.White.copy(alpha = 0.15f) 
                else 
                    HealthBlue.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            TextButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(18.dp),
                        tint = if (isDarkMode) Color.White.copy(alpha = 0.8f) else HealthBlue
                    )
                    Text(
                        "Editar",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (isDarkMode) Color.White.copy(alpha = 0.8f) else HealthBlue
                    )
                }
            }
        }
        
        // Botón Eliminar
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (canDelete) {
                    if (isDarkMode) 
                        ErrorRed.copy(alpha = 0.2f) 
                    else 
                        ErrorRed.copy(alpha = 0.1f)
                } else {
                    if (isDarkMode) 
                        Color.White.copy(alpha = 0.05f) 
                    else 
                        Color.Gray.copy(alpha = 0.1f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            TextButton(
                onClick = onDelete,
                enabled = canDelete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (canDelete) Icons.Default.Delete else Icons.Default.Block,
                        contentDescription = if (canDelete) "Eliminar" else "No se puede eliminar",
                        modifier = Modifier.size(18.dp),
                        tint = if (canDelete) {
                            ErrorRed
                        } else {
                            if (isDarkMode) Color.White.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.5f)
                        }
                    )
                    Text(
                        if (canDelete) "Eliminar" else "En uso",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (canDelete) {
                            ErrorRed
                        } else {
                            if (isDarkMode) Color.White.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.5f)
                        }
                    )
                }
            }
        }
    }
} 