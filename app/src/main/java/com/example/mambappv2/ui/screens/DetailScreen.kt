// DetailScreen.kt
package com.example.mambappv2.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.viewmodel.*
import com.example.mambappv2.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sin

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailScreen(
    monitoreo: Monitoreo?,
    lugarViewModel: LugarViewModel,
    patologiaViewModel: PatologiaViewModel,
    medicoViewModel: MedicoViewModel,
    tecnicoViewModel: TecnicoViewModel,
    solicitanteViewModel: SolicitanteViewModel,
    monitoreoViewModel: MonitoreoViewModel,
    pacienteViewModel: PacienteViewModel,
    equipoViewModel: EquipoViewModel,
    onBack: () -> Unit,
    onEdit: (Monitoreo) -> Unit
) {
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "detail_animation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )
    
    val monitoreoId = monitoreo?.id
    val monitoreoState by remember(monitoreoId) {
        monitoreoId?.let { monitoreoViewModel.getMonitoreoById(it) }
            ?: MutableStateFlow(null)
    }.collectAsState()

    val lugares by lugarViewModel.lugares.collectAsState()
    val patologias by patologiaViewModel.patologias.collectAsState()
    val medicos by medicoViewModel.medicos.collectAsState()
    val tecnicos by tecnicoViewModel.tecnicos.collectAsState()
    val solicitantes by solicitanteViewModel.solicitantes.collectAsState()
    val pacientes by pacienteViewModel.pacientes.collectAsState()
    val equipos by equipoViewModel.equipos.collectAsState()

    val monitoreoReal = monitoreoState

    val equipo = equipos.find { it.id == monitoreoReal?.idEquipo }
        ?.let { "Equipo Nº${it.numero} — ${it.descripcion}" }
        ?: monitoreoReal?.equipoSnapshot ?: "No asignado"

    val paciente = pacientes.find { it.dniPaciente == monitoreoReal?.dniPaciente }

    val lugar = lugares.find { it.id == monitoreoReal?.idLugar }
        ?.let { "${it.nombre}, ${it.provincia}" }
        ?: monitoreoReal?.lugarSnapshot ?: "Desconocido"

    val patologia = patologias.find { it.id == monitoreoReal?.idPatologia }?.nombre
        ?: monitoreoReal?.patologiaSnapshot ?: "Desconocida"

    val medico = medicos.find { it.id == monitoreoReal?.idMedico }
        ?.let { "${it.nombre} ${it.apellido}" }
        ?: monitoreoReal?.medicoSnapshot ?: "No asignado"

    val tecnico = tecnicos.find { it.id == monitoreoReal?.idTecnico }
        ?.let { "${it.nombre} ${it.apellido}" }
        ?: monitoreoReal?.tecnicoSnapshot ?: "No asignado"

    val solicitante = solicitantes.find { it.id == monitoreoReal?.idSolicitante }
        ?.let { "${it.nombre} ${it.apellido}" }
        ?: monitoreoReal?.solicitanteSnapshot ?: "No asignado"

    val openDialog = remember { mutableStateOf(false) }
    val showEditWarningDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val entidadFaltante = monitoreoReal != null && listOf(
        medicos.any { it.id == monitoreoReal.idMedico },
        tecnicos.any { it.id == monitoreoReal.idTecnico },
        lugares.any { it.id == monitoreoReal.idLugar },
        patologias.any { it.id == monitoreoReal.idPatologia },
        solicitantes.any { it.id == monitoreoReal.idSolicitante },
        equipos.any { it.id == monitoreoReal.idEquipo || monitoreoReal.idEquipo == null },
        pacientes.any { it.dniPaciente == monitoreoReal.dniPaciente }
    ).contains(false)

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo animado
        DetailBackground(isDarkMode = isDarkMode, waveOffset = waveOffset)
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ModernDetailTopBar(
                    title = "Detalle del Monitoreo",
                    subtitle = "Registro #${monitoreoReal?.nroRegistro ?: ""}",
                    onBackClick = onBack,
                    onEditClick = {
                        if (entidadFaltante) {
                            showEditWarningDialog.value = true
                        } else {
                            monitoreoReal?.let(onEdit)
                        }
                    },
                    onDeleteClick = { openDialog.value = true }
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { padding ->
            if (monitoreoReal == null) {
                EmptyDetailState(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Cards con animación de entrada
                    var visibleCards by remember { mutableStateOf(0) }
                    
                    LaunchedEffect(Unit) {
                        repeat(6) { index ->
                            kotlinx.coroutines.delay(100L * index)
                            visibleCards = index + 1
                        }
                    }
                    
                    // Card de fechas
                    AnimatedVisibility(
                        visible = visibleCards >= 1,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                    ) {
                        ModernDetailCard(
                            title = "Información del Registro",
                            icon = Icons.Default.Assignment,
                            gradientColors = listOf(HealthTeal, MedicalTeal80)
                        ) {
                            DetailInfoRow("Fecha Realizado", monitoreoReal.fechaRealizado, Icons.Default.CalendarToday)
                            DetailInfoRow("Fecha Presentado", monitoreoReal.fechaPresentado ?: "No informada", Icons.Default.Schedule)
                            DetailInfoRow("Fecha Cobrado", monitoreoReal.fechaCobrado ?: "No informada", Icons.Default.Payment)
                        }
                    }

                    // Card de paciente
                    AnimatedVisibility(
                        visible = visibleCards >= 2,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                    ) {
                        ModernDetailCard(
                            title = "Información del Paciente",
                            icon = Icons.Default.Person,
                            gradientColors = listOf(HealthBlue, MedicalBlue80)
                        ) {
                            if (paciente != null) {
                                DetailInfoRow("DNI", paciente.dniPaciente.toString(), Icons.Default.Badge)
                                DetailInfoRow("Nombre", paciente.nombre, Icons.Default.Person)
                                DetailInfoRow("Apellido", paciente.apellido, Icons.Default.Person)
                                DetailInfoRow("Edad", "${paciente.edad} años", Icons.Default.Cake)
                                DetailInfoRow("Mutual", paciente.mutual, Icons.Default.LocalHospital)
                            } else {
                                DetailInfoRow("DNI", monitoreoReal.dniPaciente.toString(), Icons.Default.Badge)
                                DetailInfoRow("Nombre", monitoreoReal.pacienteNombre, Icons.Default.Person)
                                DetailInfoRow("Apellido", monitoreoReal.pacienteApellido, Icons.Default.Person)
                                DetailInfoRow("Edad", "${monitoreoReal.pacienteEdad} años", Icons.Default.Cake)
                                DetailInfoRow("Mutual", monitoreoReal.pacienteMutual, Icons.Default.LocalHospital)
                            }
                        }
                    }

                    // Card de lugar y contexto
                    AnimatedVisibility(
                        visible = visibleCards >= 3,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                    ) {
                        ModernDetailCard(
                            title = "Contexto del Procedimiento",
                            icon = Icons.Default.Place,
                            gradientColors = listOf(HealthGreen, MedicalGreen80)
                        ) {
                            DetailInfoRow("Lugar", lugar, Icons.Default.Place)
                            DetailInfoRow("Patología", patologia, Icons.Default.MedicalServices)
                            DetailInfoRow("Equipo", equipo, Icons.Default.Devices)
                        }
                    }

                    // Card de profesionales
                    AnimatedVisibility(
                        visible = visibleCards >= 4,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                    ) {
                        ModernDetailCard(
                            title = "Equipo Profesional",
                            icon = Icons.Default.Group,
                            gradientColors = listOf(MedicalTeal40, HealthTeal)
                        ) {
                            DetailInfoRow("Médico", medico, Icons.Default.MedicalServices)
                            DetailInfoRow("Técnico", tecnico, Icons.Default.Engineering)
                            DetailInfoRow("Solicitante", solicitante, Icons.Default.RequestPage)
                        }
                    }

                    // Card de detalles clínicos
                    AnimatedVisibility(
                        visible = visibleCards >= 5,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                    ) {
                        ModernDetailCard(
                            title = "Detalles Clínicos",
                            icon = Icons.Default.Description,
                            gradientColors = listOf(MedicalBlue40, HealthBlue)
                        ) {
                            ModernLabeledBlock("Anestesia", monitoreoReal.detalleAnestesia)
                            DetailInfoRow(
                                "Complicación", 
                                if (monitoreoReal.complicacion) "Sí" else "No", 
                                if (monitoreoReal.complicacion) Icons.Default.Warning else Icons.Default.CheckCircle
                            )
                            if (monitoreoReal.complicacion) {
                                ModernLabeledBlock("Detalle Complicación", monitoreoReal.detalleComplicacion)
                            }
                            ModernLabeledBlock("Cambio de Motor", monitoreoReal.cambioMotor)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        
        // Diálogos
        if (openDialog.value) {
            ModernDeleteDialog(
                onConfirm = {
                    monitoreoReal?.let {
                        monitoreoViewModel.deleteMonitoreo(it)
                        scope.launch {
                            snackBarHostState.showSnackbar("Monitoreo eliminado correctamente")
                        }
                        onBack()
                    }
                    openDialog.value = false
                },
                onDismiss = { openDialog.value = false }
            )
        }

        if (showEditWarningDialog.value) {
            ModernWarningDialog(
                onConfirm = {
                    showEditWarningDialog.value = false
                    monitoreoReal?.let(onEdit)
                },
                onDismiss = { showEditWarningDialog.value = false }
            )
        }
    }
}

@Composable
private fun DetailBackground(
    isDarkMode: Boolean,
    waveOffset: Float
) {
    val backgroundGradient = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A2226),
            Color(0xFF0E1414)
        )
    } else {
        listOf(
            Color(0xFFFAFDFD),
            Color(0xFFE8F6F9),
            Color(0xFFFAFDFD)
        )
    }
    
    val waveColors = if (isDarkMode) {
        listOf(
            MedicalTeal80.copy(alpha = 0.04f),
            HealthBlue.copy(alpha = 0.03f)
        )
    } else {
        listOf(
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
            
            for (i in 0..1) {
                val amplitude = height * 0.03f * (i + 1)
                val frequency = 0.005f / (i + 1)
                val phaseShift = waveOffset + (i * 140f)
                
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0f, height * 0.9f)
                
                for (x in 0..width.toInt() step 12) {
                    val y = height * 0.9f + amplitude * sin((x * frequency + phaseShift) * Math.PI / 180f).toFloat()
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
private fun ModernDetailTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            
            // Botones de acción
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernDetailCard(
    title: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
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
                        colors = gradientColors,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(800f, 200f)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header del card
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.25f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Contenido del card
                content()
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun ModernLabeledBlock(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Color.White.copy(alpha = 0.9f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            )
        ) {
            Text(
                text = value.ifBlank { "Sin información" },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp),
                color = Color.White.copy(alpha = 0.95f)
            )
        }
    }
}

@Composable
private fun EmptyDetailState(
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
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Monitoreo no encontrado",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "No se pudo cargar la información del monitoreo solicitado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ModernDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "¿Eliminar monitoreo?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Text(
                "Esta acción no se puede deshacer. El registro se eliminará permanentemente.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ModernWarningDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Datos eliminados",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Text(
                "Este monitoreo contiene información de entidades que ya no existen. Al editarlo, deberás volver a seleccionar esos valores o se perderán.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Continuar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
