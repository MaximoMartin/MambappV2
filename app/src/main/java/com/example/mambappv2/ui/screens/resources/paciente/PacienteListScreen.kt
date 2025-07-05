// PacienteListScreen.kt
package com.example.mambappv2.ui.screens.resources.paciente

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
import com.example.mambappv2.data.entities.Paciente
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.ui.components.dialogs.SimpleInputDialog
import com.example.mambappv2.ui.theme.*
import com.example.mambappv2.viewmodel.PacienteViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteListScreen(
    navController: NavController,
    viewModel: PacienteViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val pacientes by viewModel.pacientes.collectAsState()
    val pacienteUsageCount by monitoreoViewModel.pacienteUsageCount.collectAsState()
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    val showDialog = remember { mutableStateOf(false) }
    val editingPaciente = remember { mutableStateOf<Paciente?>(null) }
    val showConfirmDelete = remember { mutableStateOf(false) }
    val pacienteToDelete = remember { mutableStateOf<Paciente?>(null) }

    // Estados para el diálogo moderno
    val dniField = remember { mutableStateOf("") }
    val nombreField = remember { mutableStateOf("") }
    val apellidoField = remember { mutableStateOf("") }
    val edadField = remember { mutableStateOf("") }
    val mutualField = remember { mutableStateOf("") }

    // Animaciones de fondo
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    fun resetCampos(p: Paciente? = null) {
        dniField.value = p?.dniPaciente?.toString() ?: ""
        nombreField.value = p?.nombre ?: ""
        apellidoField.value = p?.apellido ?: ""
        edadField.value = p?.edad?.toString() ?: ""
        mutualField.value = p?.mutual ?: ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo animado con colores azules para pacientes
        PacienteListBackground(isDarkMode = isDarkMode, waveOffset = waveOffset)

    Scaffold(
            containerColor = Color.Transparent,
        topBar = {
                ModernPacienteTopBar(
                    pacienteCount = pacientes.size,
                    onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
                ModernPacienteFAB(
                    onClick = {
                editingPaciente.value = null
                resetCampos()
                showDialog.value = true
                    }
                )
        }
    ) { padding ->
        if (pacientes.isEmpty()) {
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
                    itemsIndexed(pacientes) { index, paciente ->
                        val usageCount = pacienteUsageCount[paciente.dniPaciente] ?: 0
                        
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
                                title = "${paciente.nombre} ${paciente.apellido}",
                                subtitle = "DNI: ${paciente.dniPaciente} • Edad: ${paciente.edad} • ${paciente.mutual}",
                                usageCount = usageCount,
                        onEdit = {
                            editingPaciente.value = paciente
                            resetCampos(paciente)
                            showDialog.value = true
                        },
                        onDelete = {
                                    if (usageCount == 0) {
                            pacienteToDelete.value = paciente
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
                title = if (editingPaciente.value != null) "Editar Paciente" else "Nuevo Paciente",
                fields = listOf(
                    "DNI" to dniField,
                    "Nombre" to nombreField,
                    "Apellido" to apellidoField,
                    "Edad" to edadField,
                    "Mutual" to mutualField
                ),
                onDismiss = { showDialog.value = false },
                onConfirm = {
                    if (dniField.value.isNotBlank() && nombreField.value.isNotBlank() && 
                        apellidoField.value.isNotBlank() && edadField.value.isNotBlank()) {
                        try {
                            val dni = dniField.value.toInt()
                            val edad = edadField.value.toInt()
                            val actual = editingPaciente.value

                            if (actual != null) {
                        viewModel.updatePaciente(
                                    actual.copy(
                                dniPaciente = dni,
                                        nombre = nombreField.value,
                                        apellido = apellidoField.value,
                                edad = edad,
                                        mutual = mutualField.value
                            )
                        )
                            } else {
                                viewModel.addPaciente(dni, nombreField.value, apellidoField.value, edad, mutualField.value)
                    }
                    showDialog.value = false
                        } catch (e: NumberFormatException) {
                            // Mantener diálogo abierto si hay error de formato
                        }
                    }
                }
            )
        }

        // Diálogo de confirmación de eliminación moderno
        if (showConfirmDelete.value && pacienteToDelete.value != null) {
            val paciente = pacienteToDelete.value!!
            val currentUsageCount = pacienteUsageCount[paciente.dniPaciente] ?: 0
            
            ModernDeleteDialog(
                title = "¿Eliminar paciente?",
                message = if (currentUsageCount > 0) {
                    "No se puede eliminar este paciente porque está siendo usado en $currentUsageCount monitoreo(s)."
                } else {
                    "Esta acción no se puede deshacer."
                },
                canDelete = currentUsageCount == 0,
                onDismiss = { showConfirmDelete.value = false },
                onConfirm = {
                    if (currentUsageCount == 0) {
                        viewModel.deletePaciente(paciente)
                            showConfirmDelete.value = false
                        }
                },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun PacienteListBackground(
    isDarkMode: Boolean,
    waveOffset: Float
) {
    val backgroundGradient = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A2250),
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
            HealthBlue.copy(alpha = 0.03f),
            MedicalBlue80.copy(alpha = 0.025f),
            HealthTeal.copy(alpha = 0.02f)
        )
    } else {
        listOf(
            HealthBlue.copy(alpha = 0.025f),
            MedicalBlue40.copy(alpha = 0.03f),
            HealthTeal.copy(alpha = 0.02f)
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
private fun ModernPacienteTopBar(
    pacienteCount: Int,
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
                    text = "Gestión de Pacientes",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$pacienteCount pacientes registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Card(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HealthBlue.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Pacientes",
                        tint = HealthBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernPacienteFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(20.dp),
        containerColor = HealthBlue,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp
        )
    ) {
        Icon(
            Icons.Default.PersonAdd,
            contentDescription = "Agregar Paciente",
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
                    containerColor = HealthBlue.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Sin pacientes",
                        modifier = Modifier.size(40.dp),
                        tint = HealthBlue
                    )
                }
            }
            
            Text(
                text = "No hay pacientes registrados",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color(0xFF1A1A1A)
            )
            
            Text(
                text = "Presiona el botón + para agregar tu primer paciente",
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
        containerColor = if (isDarkMode) Color(0xFF1A2250) else Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
