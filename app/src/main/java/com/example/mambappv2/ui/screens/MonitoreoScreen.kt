// MonitoreoScreen.kt
package com.example.mambappv2.ui.screens

import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.ui.components.dialogs.SimpleInputDialog
import com.example.mambappv2.ui.components.monitoreo.FechaSection
import com.example.mambappv2.ui.components.monitoreo.PacienteSection
import com.example.mambappv2.ui.components.sections.*
import com.example.mambappv2.ui.state.MonitoreoFormState
import com.example.mambappv2.ui.theme.*
import com.example.mambappv2.viewmodel.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonitoreoScreen(
    monitoreo: Monitoreo? = null,
    viewModel: MonitoreoViewModel,
    pacienteViewModel: PacienteViewModel,
    medicoViewModel: MedicoViewModel,
    tecnicoViewModel: TecnicoViewModel,
    lugarViewModel: LugarViewModel,
    patologiaViewModel: PatologiaViewModel,
    solicitanteViewModel: SolicitanteViewModel,
    equipoViewModel: EquipoViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val formState = remember { MonitoreoFormState() }
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "monitoreo_animation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    // Precargar datos si es edición
    LaunchedEffect(monitoreo) {
        monitoreo?.let {
            formState.fechaRealizado = it.fechaRealizado
            formState.fechaPresentado = it.fechaPresentado.orEmpty()
            formState.fechaCobrado = it.fechaCobrado.orEmpty()
            formState.dniPaciente = it.dniPaciente.toString()
            formState.selectedMedicoId = it.idMedico
            formState.selectedTecnicoId = it.idTecnico
            formState.selectedLugarId = it.idLugar
            formState.selectedPatologiaId = it.idPatologia
            formState.selectedSolicitanteId = it.idSolicitante
            formState.anestesia = it.detalleAnestesia
            formState.complicacion = it.complicacion
            formState.detalleComplicacion = it.detalleComplicacion
            formState.cambioMotor = it.cambioMotor
            formState.selectedEquipoId = it.idEquipo ?: -1
        }
    }

    val pacientes by pacienteViewModel.pacientes.collectAsState()
    val paciente = pacientes.find { it.dniPaciente.toString() == formState.dniPaciente }

    val medicos by medicoViewModel.medicos.collectAsState()
    val tecnicos by tecnicoViewModel.tecnicos.collectAsState()
    val solicitantes by solicitanteViewModel.solicitantes.collectAsState()
    val lugares by lugarViewModel.lugares.collectAsState()
    val patologias by patologiaViewModel.patologias.collectAsState()
    val equipos by equipoViewModel.equipos.collectAsState()

    fun saveMonitoreo() {
        val hoy = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fechasValidas = try {
            val f1 = LocalDate.parse(formState.fechaRealizado, formatter)
            val f2 = formState.fechaPresentado.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it, formatter) }
            val f3 = formState.fechaCobrado.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it, formatter) }
            !f1.isAfter(hoy) && (f2 == null || !f2.isAfter(hoy)) && (f3 == null || !f3.isAfter(hoy))
        } catch (e: Exception) {
            false
        }

        if (!fechasValidas || formState.dniPaciente.isBlank()) {
            Toast.makeText(context, "Fechas inválidas o DNI vacío", Toast.LENGTH_LONG).show()
            return
        }

        val pacienteExiste = pacientes.any { it.dniPaciente.toString() == formState.dniPaciente }
        if (!pacienteExiste) {
            Toast.makeText(context, "Debes agregar un paciente válido antes de guardar", Toast.LENGTH_LONG).show()
            return
        }

        val paciente = pacientes.find { it.dniPaciente.toString() == formState.dniPaciente }

        val monitoreoFinal = monitoreo?.copy(
            fechaRealizado = formState.fechaRealizado,
            fechaPresentado = formState.fechaPresentado.ifBlank { null },
            fechaCobrado = formState.fechaCobrado.ifBlank { null },
            dniPaciente = formState.dniPaciente.toInt(),
            idMedico = formState.selectedMedicoId,
            idTecnico = formState.selectedTecnicoId,
            idLugar = formState.selectedLugarId,
            idPatologia = formState.selectedPatologiaId,
            idSolicitante = formState.selectedSolicitanteId,
            detalleAnestesia = formState.anestesia,
            complicacion = formState.complicacion,
            detalleComplicacion = formState.detalleComplicacion,
            cambioMotor = formState.cambioMotor,
            idEquipo = if (formState.selectedEquipoId != -1) formState.selectedEquipoId else null,
            // Snapshots: conservar si no se modificó el ID
            medicoSnapshot = if (monitoreo.idMedico == formState.selectedMedicoId)
                monitoreo.medicoSnapshot
            else
                medicos.find { it.id == formState.selectedMedicoId }?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido",

            tecnicoSnapshot = if (monitoreo.idTecnico == formState.selectedTecnicoId)
                monitoreo.tecnicoSnapshot
            else
                tecnicos.find { it.id == formState.selectedTecnicoId }?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido",

            solicitanteSnapshot = if (monitoreo.idSolicitante == formState.selectedSolicitanteId)
                monitoreo.solicitanteSnapshot
            else
                solicitantes.find { it.id == formState.selectedSolicitanteId }?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido",

            lugarSnapshot = if (monitoreo.idLugar == formState.selectedLugarId)
                monitoreo.lugarSnapshot
            else
                lugares.find { it.id == formState.selectedLugarId }?.let { "${it.nombre}, ${it.provincia}" } ?: "Desconocido",

            patologiaSnapshot = if (monitoreo.idPatologia == formState.selectedPatologiaId)
                monitoreo.patologiaSnapshot
            else
                patologias.find { it.id == formState.selectedPatologiaId }?.nombre ?: "Desconocido",

            equipoSnapshot = if (monitoreo.idEquipo == formState.selectedEquipoId)
                monitoreo.equipoSnapshot
            else
                equipos.find { it.id == formState.selectedEquipoId }?.let { "Equipo Nº${it.numero} — ${it.descripcion}" } ?: "No asignado",

            pacienteNombre = if (monitoreo.dniPaciente.toString() == formState.dniPaciente)
                monitoreo.pacienteNombre
            else
                paciente?.nombre ?: "",

            pacienteApellido = if (monitoreo.dniPaciente.toString() == formState.dniPaciente)
                monitoreo.pacienteApellido
            else
                paciente?.apellido ?: "",

            pacienteEdad = if (monitoreo.dniPaciente.toString() == formState.dniPaciente)
                monitoreo.pacienteEdad
            else
                paciente?.edad ?: 0,

            pacienteMutual = if (monitoreo.dniPaciente.toString() == formState.dniPaciente)
                monitoreo.pacienteMutual
            else
                paciente?.mutual ?: ""
        ) ?: Monitoreo(
            nroRegistro = viewModel.monitoreos.value.maxOfOrNull { it.nroRegistro }?.plus(1) ?: 1,
            fechaRealizado = formState.fechaRealizado,
            fechaPresentado = formState.fechaPresentado.ifBlank { null },
            fechaCobrado = formState.fechaCobrado.ifBlank { null },
            dniPaciente = formState.dniPaciente.toInt(),
            idMedico = formState.selectedMedicoId,
            idTecnico = formState.selectedTecnicoId,
            idLugar = formState.selectedLugarId,
            idPatologia = formState.selectedPatologiaId,
            idSolicitante = formState.selectedSolicitanteId,
            detalleAnestesia = formState.anestesia,
            complicacion = formState.complicacion,
            detalleComplicacion = formState.detalleComplicacion,
            cambioMotor = formState.cambioMotor,
            idEquipo = if (formState.selectedEquipoId != -1) formState.selectedEquipoId else null,
            // Snapshots nuevos (solo en creación)
            medicoSnapshot = medicos.find { it.id == formState.selectedMedicoId }?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido",
            tecnicoSnapshot = tecnicos.find { it.id == formState.selectedTecnicoId }?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido",
            solicitanteSnapshot = solicitantes.find { it.id == formState.selectedSolicitanteId }?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido",
            lugarSnapshot = lugares.find { it.id == formState.selectedLugarId }?.let { "${it.nombre}, ${it.provincia}" } ?: "Desconocido",
            patologiaSnapshot = patologias.find { it.id == formState.selectedPatologiaId }?.nombre ?: "Desconocido",
            equipoSnapshot = equipos.find { it.id == formState.selectedEquipoId }?.let { "Equipo Nº${it.numero} — ${it.descripcion}" } ?: "No asignado",
            pacienteNombre = paciente?.nombre ?: "",
            pacienteApellido = paciente?.apellido ?: "",
            pacienteEdad = paciente?.edad ?: 0,
            pacienteMutual = paciente?.mutual ?: ""
        )

        if (monitoreo != null) {
            viewModel.updateMonitoreo(monitoreoFinal)
        } else {
            viewModel.addMonitoreo(monitoreoFinal)
        }

        onSaveSuccess()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo animado
        MonitoreoBackground(isDarkMode = isDarkMode, waveOffset = waveOffset)
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ModernMonitoreoTopBar(
                    title = if (monitoreo != null) "Editar Monitoreo" else "Nuevo Monitoreo",
                    subtitle = if (monitoreo != null) "Registro #${monitoreo.nroRegistro}" else "Completá los campos",
                    onBackClick = onBack
                )
            },
            floatingActionButton = {
                ModernSaveButton(onClick = { saveMonitoreo() })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Animación de entrada para las secciones
                var visibleSections by remember { mutableStateOf(0) }
                
                LaunchedEffect(Unit) {
                    repeat(7) { index ->
                        kotlinx.coroutines.delay(150L * index)
                        visibleSections = index + 1
                    }
                }

                // 1. Sección de Fechas
                AnimatedVisibility(
                    visible = visibleSections >= 1,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    ModernSectionCard(
                        title = "Fechas del Procedimiento",
                        icon = Icons.Default.CalendarToday,
                        gradientColors = listOf(HealthTeal, MedicalTeal80)
                    ) {
                        FechaSection(
                            fechaRealizado = formState.fechaRealizado,
                            onFechaRealizadoChange = { formState.fechaRealizado = it },
                            fechaPresentado = formState.fechaPresentado,
                            onFechaPresentadoChange = { formState.fechaPresentado = it },
                            fechaCobrado = formState.fechaCobrado,
                            onFechaCobradoChange = { formState.fechaCobrado = it }
                        )
                    }
                }

                // 2. Sección de Paciente
                AnimatedVisibility(
                    visible = visibleSections >= 2,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    ModernSectionCard(
                        title = "Información del Paciente",
                        icon = Icons.Default.Person,
                        gradientColors = listOf(HealthBlue, MedicalBlue80)
                    ) {
                        PacienteSection(
                            dniPaciente = formState.dniPaciente,
                            onDniChange = { formState.dniPaciente = it },
                            paciente = paciente,
                            onAgregarClick = {
                                formState.showDialogTipo = "Paciente"
                                formState.setCamposDialog("DNI", "Nombre", "Apellido", "Edad", "Mutual")
                                formState.camposDialog[0].second.value = formState.dniPaciente
                            },
                            onEditarClick = {
                                formState.showDialogTipo = "Paciente"
                                formState.setCamposDialog("DNI", "Nombre", "Apellido", "Edad", "Mutual")
                                formState.camposDialog[0].second.value = paciente?.dniPaciente?.toString() ?: ""
                                formState.camposDialog[1].second.value = paciente?.nombre ?: ""
                                formState.camposDialog[2].second.value = paciente?.apellido ?: ""
                                formState.camposDialog[3].second.value = paciente?.edad?.toString() ?: ""
                                formState.camposDialog[4].second.value = paciente?.mutual ?: ""
                            }
                        )
                    }
                }

                // 3. Sección de Profesionales
                AnimatedVisibility(
                    visible = visibleSections >= 3,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    ModernSectionCard(
                        title = "Equipo Profesional",
                        icon = Icons.Default.Group,
                        gradientColors = listOf(HealthGreen, MedicalGreen80)
                    ) {
                        ProfesionalesSection(
                            medicos = medicos,
                            selectedMedicoId = formState.selectedMedicoId,
                            onMedicoSelected = { formState.selectedMedicoId = it },
                            onAddMedico = {
                                formState.showDialogTipo = "Médico"
                                formState.setCamposDialog("Nombre", "Apellido")
                            },
                            tecnicos = tecnicos,
                            selectedTecnicoId = formState.selectedTecnicoId,
                            onTecnicoSelected = { formState.selectedTecnicoId = it },
                            onAddTecnico = {
                                formState.showDialogTipo = "Técnico"
                                formState.setCamposDialog("Nombre", "Apellido")
                            },
                            solicitantes = solicitantes,
                            selectedSolicitanteId = formState.selectedSolicitanteId,
                            onSolicitanteSelected = { formState.selectedSolicitanteId = it },
                            onAddSolicitante = {
                                formState.showDialogTipo = "Solicitante"
                                formState.setCamposDialog("Nombre", "Apellido")
                            }
                        )
                    }
                }

                // 4. Sección de Lugar
                AnimatedVisibility(
                    visible = visibleSections >= 4,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    ModernSectionCard(
                        title = "Lugar del Procedimiento",
                        icon = Icons.Default.Place,
                        gradientColors = listOf(MedicalTeal40, HealthTeal)
                    ) {
                        LugarSection(
                            lugares = lugares,
                            selectedLugarId = formState.selectedLugarId,
                            onLugarSelected = { formState.selectedLugarId = it },
                            onAddLugar = {
                                formState.showDialogTipo = "Lugar"
                                formState.setCamposDialog("Nombre", "Provincia")
                            }
                        )
                    }
                }

                // 5. Sección de Patología
                AnimatedVisibility(
                    visible = visibleSections >= 5,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    ModernSectionCard(
                        title = "Patología a Monitorear",
                        icon = Icons.Default.MedicalServices,
                        gradientColors = listOf(MedicalBlue40, HealthBlue)
                    ) {
                        PatologiaSection(
                            patologias = patologias,
                            selectedPatologiaId = formState.selectedPatologiaId,
                            onPatologiaSelected = { formState.selectedPatologiaId = it },
                            onAddPatologia = {
                                formState.showDialogTipo = "Patología"
                                formState.setCamposDialog("Nombre")
                            }
                        )
                    }
                }

                // 6. Sección de Detalles Clínicos
                AnimatedVisibility(
                    visible = visibleSections >= 6,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    ModernSectionCard(
                        title = "Detalles Clínicos",
                        icon = Icons.Default.Description,
                        gradientColors = listOf(MedicalGreen40, HealthGreen)
                    ) {
                        DetalleClinicoSection(
                            anestesia = formState.anestesia,
                            onAnestesiaChange = { formState.anestesia = it },
                            complicacion = formState.complicacion,
                            onComplicacionChange = { formState.complicacion = it },
                            detalleComplicacion = formState.detalleComplicacion,
                            onDetalleComplicacionChange = { formState.detalleComplicacion = it },
                            cambioMotor = formState.cambioMotor,
                            onCambioMotorChange = { formState.cambioMotor = it }
                        )
                    }
                }

                // 7. Sección de Equipos
                AnimatedVisibility(
                    visible = visibleSections >= 7,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    ModernSectionCard(
                        title = "Equipos Utilizados",
                        icon = Icons.Default.Devices,
                        gradientColors = listOf(HealthOrange, Color(0xFFFFB74D))
                    ) {
                        EquipoSection(
                            equipos = equipos,
                            selectedEquipoId = formState.selectedEquipoId,
                            onEquipoSelected = { formState.selectedEquipoId = it },
                            onAddEquipo = {
                                formState.showDialogTipo = "Equipo"
                                formState.setCamposDialog("Número", "Descripción")
                            }
                        )
                    }
                }

                // Espaciado final para el FAB
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Diálogo de entidades
        if (formState.showDialogTipo.isNotEmpty()) {
            SimpleInputDialog(
                title = "Nuevo ${formState.showDialogTipo}",
                fields = formState.camposDialog,
                onDismiss = { formState.showDialogTipo = "" },
                onConfirm = {
                    when (formState.showDialogTipo) {
                        "Paciente" -> {
                            val dni = formState.camposDialog[0].second.value.toIntOrNull() ?: return@SimpleInputDialog
                            pacienteViewModel.addPaciente(
                                dni = dni,
                                nombre = formState.camposDialog[1].second.value,
                                apellido = formState.camposDialog[2].second.value,
                                edad = formState.camposDialog[3].second.value.toIntOrNull() ?: 0,
                                mutual = formState.camposDialog[4].second.value
                            )
                            formState.dniPaciente = dni.toString()
                        }
                        "Médico" -> {
                            medicoViewModel.addMedico(
                                nombre = formState.camposDialog[0].second.value,
                                apellido = formState.camposDialog[1].second.value
                            )
                        }
                        "Técnico" -> {
                            tecnicoViewModel.addTecnico(
                                nombre = formState.camposDialog[0].second.value,
                                apellido = formState.camposDialog[1].second.value
                            )
                        }
                        "Solicitante" -> {
                            solicitanteViewModel.addSolicitante(
                                nombre = formState.camposDialog[0].second.value,
                                apellido = formState.camposDialog[1].second.value
                            )
                        }
                        "Lugar" -> {
                            lugarViewModel.addLugar(
                                nombre = formState.camposDialog[0].second.value,
                                provincia = formState.camposDialog[1].second.value
                            )
                        }
                        "Patología" -> {
                            patologiaViewModel.addPatologia(
                                nombre = formState.camposDialog[0].second.value
                            )
                        }
                        "Equipo" -> {
                            equipoViewModel.addEquipo(
                                numero = formState.camposDialog[0].second.value,
                                descripcion = formState.camposDialog[1].second.value
                            )
                        }
                    }
                    formState.showDialogTipo = ""
                }
            )
        }
    }
}

@Composable
private fun MonitoreoBackground(
    isDarkMode: Boolean,
    waveOffset: Float
) {
    val backgroundGradient = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A2822),
            Color(0xFF0E1414)
        )
    } else {
        listOf(
            Color(0xFFFAFDFD),
            Color(0xFFE8F9F6),
            Color(0xFFFAFDFD)
        )
    }
    
    val waveColors = if (isDarkMode) {
        listOf(
            MedicalTeal80.copy(alpha = 0.03f),
            HealthGreen.copy(alpha = 0.02f),
            MedicalBlue80.copy(alpha = 0.025f)
        )
    } else {
        listOf(
            MedicalTeal40.copy(alpha = 0.025f),
            HealthGreen.copy(alpha = 0.02f),
            HealthBlue.copy(alpha = 0.03f)
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
                val frequency = 0.004f / (i + 1)
                val phaseShift = waveOffset + (i * 100f)
                
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0f, height * 0.92f)
                
                for (x in 0..width.toInt() step 15) {
                    val y = height * 0.92f + amplitude * sin((x * frequency + phaseShift) * Math.PI / 180f).toFloat()
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
private fun ModernMonitoreoTopBar(
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
                        Icons.Default.MonitorHeart,
                        contentDescription = "Monitoreo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernSectionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                        end = androidx.compose.ui.geometry.Offset(600f, 150f)
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
                        modifier = Modifier.size(36.dp),
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
                                modifier = Modifier.size(18.dp),
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
private fun ModernSaveButton(
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
            Icons.Default.Check,
            contentDescription = "Guardar",
            modifier = Modifier.size(28.dp)
        )
    }
}
