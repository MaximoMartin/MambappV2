// TecnicoListScreen.kt
package com.example.mambappv2.ui.screens.resources.tecnico

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
import com.example.mambappv2.data.entities.Tecnico
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.ui.components.dialogs.SimpleInputDialog
import com.example.mambappv2.ui.theme.*
import com.example.mambappv2.viewmodel.TecnicoViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecnicoListScreen(
    navController: NavController,
    viewModel: TecnicoViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val tecnicos by viewModel.tecnicos.collectAsState()
    val tecnicoUsageCount by monitoreoViewModel.tecnicoUsageCount.collectAsState()
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    val showDialog = remember { mutableStateOf(false) }
    val editingTecnico = remember { mutableStateOf<Tecnico?>(null) }
    val showConfirmDelete = remember { mutableStateOf(false) }
    val tecnicoToDelete = remember { mutableStateOf<Tecnico?>(null) }

    // Estados para el diálogo moderno
    val nombreField = remember { mutableStateOf("") }
    val apellidoField = remember { mutableStateOf("") }

    // Animaciones de fondo
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    fun resetCampos(tecnico: Tecnico? = null) {
        nombreField.value = tecnico?.nombre ?: ""
        apellidoField.value = tecnico?.apellido ?: ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo animado con colores teal para técnicos
        TecnicoListBackground(isDarkMode = isDarkMode, waveOffset = waveOffset)
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ModernTecnicoTopBar(
                    tecnicoCount = tecnicos.size,
                    onBackClick = { navController.popBackStack() }
                )
            },
            floatingActionButton = {
                ModernTecnicoFAB(
                    onClick = {
                        editingTecnico.value = null
                        resetCampos()
                        showDialog.value = true
                    }
                )
            }
        ) { padding ->
            if (tecnicos.isEmpty()) {
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
                    itemsIndexed(tecnicos) { index, tecnico ->
                        val usageCount = tecnicoUsageCount[tecnico.id] ?: 0
                        
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
                                title = "${tecnico.nombre} ${tecnico.apellido}",
                                subtitle = "ID: ${tecnico.id}",
                                usageCount = usageCount,
                                onEdit = {
                                    editingTecnico.value = tecnico
                                    resetCampos(tecnico)
                                    showDialog.value = true
                                },
                                onDelete = {
                                    if (usageCount == 0) {
                                        tecnicoToDelete.value = tecnico
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
                title = if (editingTecnico.value != null) "Editar Técnico" else "Nuevo Técnico",
                fields = listOf(
                    "Nombre" to nombreField,
                    "Apellido" to apellidoField
                ),
                onDismiss = { showDialog.value = false },
                onConfirm = {
                    if (nombreField.value.isNotBlank() && apellidoField.value.isNotBlank()) {
                        val actual = editingTecnico.value
                        if (actual != null) {
                            viewModel.updateTecnico(actual.copy(nombre = nombreField.value, apellido = apellidoField.value))
                        } else {
                            viewModel.addTecnico(nombreField.value, apellidoField.value)
                        }
                        showDialog.value = false
                    }
                }
            )
        }

        // Diálogo de confirmación de eliminación moderno
        if (showConfirmDelete.value && tecnicoToDelete.value != null) {
            val tecnico = tecnicoToDelete.value!!
            val currentUsageCount = tecnicoUsageCount[tecnico.id] ?: 0
            
            ModernDeleteDialog(
                title = "¿Eliminar técnico?",
                message = if (currentUsageCount > 0) {
                    "No se puede eliminar este técnico porque está siendo usado en $currentUsageCount monitoreo(s)."
                } else {
                    "Esta acción no se puede deshacer."
                },
                canDelete = currentUsageCount == 0,
                onDismiss = { showConfirmDelete.value = false },
                onConfirm = {
                    if (currentUsageCount == 0) {
                        viewModel.deleteTecnico(tecnico)
                        showConfirmDelete.value = false
                    }
                },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun TecnicoListBackground(
    isDarkMode: Boolean,
    waveOffset: Float
) {
    val backgroundGradient = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A3850),
            Color(0xFF0E1414)
        )
    } else {
        listOf(
            Color(0xFFFAFDFD),
            Color(0xFFE8F4F8),
            Color(0xFFFAFDFD)
        )
    }
    
    val waveColors = if (isDarkMode) {
        listOf(
            HealthTeal.copy(alpha = 0.03f),
            MedicalTeal80.copy(alpha = 0.025f),
            HealthBlue.copy(alpha = 0.02f)
        )
    } else {
        listOf(
            HealthTeal.copy(alpha = 0.025f),
            MedicalTeal40.copy(alpha = 0.03f),
            HealthBlue.copy(alpha = 0.02f)
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
private fun ModernTecnicoTopBar(
    tecnicoCount: Int,
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
                    text = "Gestión de Técnicos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$tecnicoCount técnicos registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Card(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HealthTeal.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Engineering,
                        contentDescription = "Técnicos",
                        tint = HealthTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernTecnicoFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(20.dp),
        containerColor = HealthTeal,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp
        )
    ) {
        Icon(
            Icons.Default.PersonAdd,
            contentDescription = "Agregar Técnico",
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
                    containerColor = HealthTeal.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Engineering,
                        contentDescription = "Sin técnicos",
                        modifier = Modifier.size(40.dp),
                        tint = HealthTeal
                    )
                }
            }
            
            Text(
                text = "No hay técnicos registrados",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color(0xFF1A1A1A)
            )
            
            Text(
                text = "Presiona el botón + para agregar tu primer técnico",
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
        containerColor = if (isDarkMode) Color(0xFF1A3850) else Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
