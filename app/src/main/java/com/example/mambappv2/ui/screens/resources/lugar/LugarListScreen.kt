// LugarListScreen.kt
package com.example.mambappv2.ui.screens.resources.lugar

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.navigation.NavController
import com.example.mambappv2.data.entities.Lugar
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.ui.components.dialogs.SimpleInputDialog
import com.example.mambappv2.ui.theme.*
import com.example.mambappv2.viewmodel.LugarViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LugarListScreen(
    navController: NavController,
    viewModel: LugarViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val lugares by viewModel.lugares.collectAsState()
    val lugarUsageCount by monitoreoViewModel.lugarUsageCount.collectAsState()
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    val showDialog = remember { mutableStateOf(false) }
    val editingLugar = remember { mutableStateOf<Lugar?>(null) }
    val showConfirmDelete = remember { mutableStateOf(false) }
    val lugarToDelete = remember { mutableStateOf<Lugar?>(null) }

    // Estados para el diálogo moderno
    val nombreField = remember { mutableStateOf("") }
    val provinciaField = remember { mutableStateOf("") }

    // Animaciones de fondo
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    fun resetCampos(lugar: Lugar? = null) {
        nombreField.value = lugar?.nombre ?: ""
        provinciaField.value = lugar?.provincia ?: ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo animado con colores cyan para lugares
        LugarListBackground(isDarkMode = isDarkMode, waveOffset = waveOffset)
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ModernLugarTopBar(
                    lugarCount = lugares.size,
                    onBackClick = { navController.popBackStack() }
                )
            },
            floatingActionButton = {
                ModernLugarFAB(
                    onClick = {
                        editingLugar.value = null
                        resetCampos()
                        showDialog.value = true
                    }
                )
            }
        ) { padding ->
            if (lugares.isEmpty()) {
                ModernEmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    isDarkMode = isDarkMode
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    itemsIndexed(lugares) { index, lugar ->
                        val usageCount = lugarUsageCount[lugar.id] ?: 0
                        
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = index * 100
                                )
                            ) + slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 100
                                )
                            )
                        ) {
                            ResourceCard(
                                title = "${lugar.nombre}, ${lugar.provincia}",
                                subtitle = "ID: ${lugar.id}",
                                usageCount = usageCount,
                                onEdit = {
                                    editingLugar.value = lugar
                                    resetCampos(lugar)
                                    showDialog.value = true
                                },
                                onDelete = {
                                    if (usageCount == 0) {
                                        lugarToDelete.value = lugar
                                        showConfirmDelete.value = true
                                    }
                                }
                            )
                        }
                    }
                    
                    // Espaciado final para el FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Diálogo moderno para agregar/editar
        if (showDialog.value) {
            SimpleInputDialog(
                title = if (editingLugar.value != null) "Editar Lugar" else "Nuevo Lugar",
                fields = listOf(
                    "Nombre" to nombreField,
                    "Provincia" to provinciaField
                ),
                onDismiss = { showDialog.value = false },
                onConfirm = {
                    if (nombreField.value.isNotBlank() && provinciaField.value.isNotBlank()) {
                        val actual = editingLugar.value
                        if (actual != null) {
                            viewModel.updateLugar(actual.copy(nombre = nombreField.value, provincia = provinciaField.value))
                        } else {
                            viewModel.addLugar(nombreField.value, provinciaField.value)
                        }
                        showDialog.value = false
                    }
                }
            )
        }

        // Diálogo de confirmación de eliminación moderno
        if (showConfirmDelete.value && lugarToDelete.value != null) {
            val lugar = lugarToDelete.value!!
            val currentUsageCount = lugarUsageCount[lugar.id] ?: 0
            
            ModernDeleteDialog(
                title = "¿Eliminar lugar?",
                message = if (currentUsageCount > 0) {
                    "No se puede eliminar este lugar porque está siendo usado en $currentUsageCount monitoreo(s)."
                } else {
                    "Esta acción no se puede deshacer."
                },
                canDelete = currentUsageCount == 0,
                onDismiss = { showConfirmDelete.value = false },
                onConfirm = {
                    if (currentUsageCount == 0) {
                        viewModel.deleteLugar(lugar)
                        showConfirmDelete.value = false
                    }
                },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun LugarListBackground(
    isDarkMode: Boolean,
    waveOffset: Float
) {
    val backgroundGradient = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A4F50),
            Color(0xFF0E1414)
        )
    } else {
        listOf(
            Color(0xFFFAFDFD),
            Color(0xFFE0F7FA),
            Color(0xFFFAFDFD)
        )
    }
    
    val waveColors = if (isDarkMode) {
        listOf(
            Color(0xFF00E5FF).copy(alpha = 0.03f),
            Color(0xFF00BCD4).copy(alpha = 0.025f),
            Color(0xFF26C6DA).copy(alpha = 0.02f)
        )
    } else {
        listOf(
            Color(0xFF4DD0E1).copy(alpha = 0.025f),
            Color(0xFF00BCD4).copy(alpha = 0.03f),
            Color(0xFF00ACC1).copy(alpha = 0.02f)
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
            
            for (i in 0..2) {
                val amplitude = height * 0.025f * (i + 1)
                val frequency = 0.0035f / (i + 1)
                val phaseShift = waveOffset + (i * 90f)
                
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0f, height * 0.94f)
                
                for (x in 0..width.toInt() step 18) {
                    val y = height * 0.94f + amplitude * sin((x * frequency + phaseShift) * Math.PI / 180f).toFloat()
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
private fun ModernLugarTopBar(
    lugarCount: Int,
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
                    text = "Gestión de Lugares",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$lugarCount lugares registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Card(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00BCD4).copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Lugares",
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernLugarFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color(0xFF00BCD4),
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp
        )
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Agregar Lugar",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun ModernEmptyState(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00BCD4).copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Sin lugares",
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFF00BCD4)
                    )
                }
            }
            
            Text(
                text = "No hay lugares registrados",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color(0xFF1A1A1A)
            )
            
            Text(
                text = "Presiona el botón + para agregar tu primer lugar",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun ModernDeleteDialog(
    title: String,
    message: String,
    canDelete: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isDarkMode: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (canDelete) Icons.Default.Warning else Icons.Default.Block,
                    contentDescription = "Advertencia",
                    tint = if (canDelete) WarningOrange else ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
                Text(title)
            }
        },
        text = { Text(message) },
        confirmButton = {
            if (canDelete) {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ErrorRed
                    )
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (canDelete) "Cancelar" else "Entendido")
            }
        },
        containerColor = if (isDarkMode) Color(0xFF1A4F50) else Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
