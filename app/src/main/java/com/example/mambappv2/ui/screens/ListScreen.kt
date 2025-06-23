// ListScreen.kt
package com.example.mambappv2.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.viewmodel.MonitoreoViewModel
import com.example.mambappv2.viewmodel.LugarViewModel
import com.example.mambappv2.ui.theme.*
import kotlin.math.sin
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: MonitoreoViewModel,
    lugarViewModel: LugarViewModel,
    navController: NavController
) {
    val monitoreos by viewModel.monitoreos.collectAsState()
    val lugares by lugarViewModel.lugares.collectAsState()
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "list_animation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo animado
        ListBackground(isDarkMode = isDarkMode, waveOffset = waveOffset)
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ModernListTopBar(
                    title = "Historial de Monitoreos",
                    subtitle = "${monitoreos.size} registros",
                    onBackClick = { navController.popBackStack() }
                )
            }
        ) { padding ->
            if (monitoreos.isEmpty()) {
                EmptyStateSection(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    itemsIndexed(monitoreos) { index, monitoreo ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 600,
                                    delayMillis = index * 50
                                )
                            ) + slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 600,
                                    delayMillis = index * 50
                                )
                            )
                        ) {
                            ModernMonitoreoCard(
                                monitoreo = monitoreo,
                                lugarNombre = lugares.find { it.id == monitoreo.idLugar }?.nombre ?: "Lugar desconocido",
                                index = index,
                                onClick = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("monitoreo", monitoreo)
                                    navController.navigate("detail")
                                }
                            )
                        }
                    }
                    
                    // Spacer final
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ListBackground(
    isDarkMode: Boolean,
    waveOffset: Float
) {
    val backgroundGradient = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A2228),
            Color(0xFF0E1414)
        )
    } else {
        listOf(
            Color(0xFFFAFDFD),
            Color(0xFFE8F6F8),
            Color(0xFFFAFDFD)
        )
    }
    
    val waveColors = if (isDarkMode) {
        listOf(
            MedicalBlue80.copy(alpha = 0.06f),
            HealthTeal.copy(alpha = 0.04f)
        )
    } else {
        listOf(
            MedicalBlue40.copy(alpha = 0.05f),
            HealthTeal.copy(alpha = 0.03f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(backgroundGradient)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            for (i in 0..1) {
                val amplitude = height * 0.04f * (i + 1)
                val frequency = 0.006f / (i + 1)
                val phaseShift = waveOffset + (i * 120f)
                
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0f, height * 0.85f)
                
                for (x in 0..width.toInt() step 10) {
                    val y = height * 0.85f + amplitude * sin((x * frequency + phaseShift) * Math.PI / 180f).toFloat()
                    path.lineTo(x.toFloat(), y)
                }
                
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
                
                drawPath(path, waveColors[i])
            }
        }
    }
}

@Composable
private fun ModernListTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Card(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = "Historial",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernMonitoreoCard(
    monitoreo: Monitoreo,
    lugarNombre: String,
    index: Int,
    onClick: () -> Unit
) {
    val cardColors = getMonitoreoCardColors(index)
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = cardColors,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 100f)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header del card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.25f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Registro #${monitoreo.nroRegistro}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White
                            )
                        }
                    }
                    
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Ver detalles",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Información principal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Columna izquierda
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRow(
                            icon = Icons.Default.Person,
                            label = "DNI",
                            value = monitoreo.dniPaciente.toString(),
                            isWhite = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        InfoRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Fecha",
                            value = monitoreo.fechaRealizado,
                            isWhite = true
                        )
                    }
                    
                    // Columna derecha
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRow(
                            icon = Icons.Default.Place,
                            label = "Lugar",
                            value = lugarNombre,
                            isWhite = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        InfoRow(
                            icon = Icons.Default.Schedule,
                            label = "Estado",
                            value = getEstadoText(monitoreo),
                            isWhite = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isWhite: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = if (isWhite) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isWhite) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isWhite) Color.White else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyStateSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = "Sin registros",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No hay monitoreos registrados",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Los registros aparecerán aquí una vez que comiences a crear monitoreos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getMonitoreoCardColors(index: Int): List<Color> {
    return when (index % 4) {
        0 -> listOf(HealthTeal, MedicalTeal80)
        1 -> listOf(HealthBlue, MedicalBlue80)
        2 -> listOf(MedicalTeal40, HealthTeal)
        else -> listOf(MedicalBlue40, HealthBlue)
    }
}

private fun getEstadoText(monitoreo: Monitoreo): String {
    return when {
        !monitoreo.fechaCobrado.isNullOrBlank() -> "Cobrado"
        !monitoreo.fechaPresentado.isNullOrBlank() -> "Presentado"
        else -> "Realizado"
    }
}
