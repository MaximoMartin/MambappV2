// DetailScreen.kt
package com.example.mambappv2.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.viewmodel.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🗂️ Detalle del Monitoreo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (entidadFaltante) {
                            showEditWarningDialog.value = true
                        } else {
                            monitoreoReal?.let(onEdit)
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { openDialog.value = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        if (monitoreoReal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("❌ No se encontró ningún monitoreo")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailCard(title = "🧾 Registro #${monitoreoReal.nroRegistro}") {
                    InfoLine("Fecha Realizado", monitoreoReal.fechaRealizado)
                    InfoLine("Fecha Presentado", monitoreoReal.fechaPresentado ?: "No informada")
                    InfoLine("Fecha Cobrado", monitoreoReal.fechaCobrado ?: "No informada")
                }

                DetailCard(title = "👤 Paciente") {
                    if (paciente != null) {
                        InfoLine("DNI", paciente.dniPaciente.toString())
                        InfoLine("Nombre", paciente.nombre)
                        InfoLine("Apellido", paciente.apellido)
                        InfoLine("Edad", paciente.edad.toString())
                        InfoLine("Mutual", paciente.mutual)
                    } else {
                        InfoLine("DNI", monitoreoReal.dniPaciente.toString())
                        InfoLine("Nombre", monitoreoReal.pacienteNombre)
                        InfoLine("Apellido", monitoreoReal.pacienteApellido)
                        InfoLine("Edad", monitoreoReal.pacienteEdad.toString())
                        InfoLine("Mutual", monitoreoReal.pacienteMutual)
                    }
                }

                DetailCard(title = "📍 Lugar, Patología y Equipo") {
                    InfoLine("Lugar", lugar)
                    InfoLine("Patología", patologia)
                    InfoLine("Equipo", equipo)
                }

                DetailCard(title = "👥 Profesionales") {
                    InfoLine("Médico", medico)
                    InfoLine("Técnico", tecnico)
                    InfoLine("Solicitante", solicitante)
                }

                DetailCard(title = "📝 Detalles Clínicos") {
                    LabeledBlock("Anestesia", monitoreoReal.detalleAnestesia)
                    InfoLine("Complicación", if (monitoreoReal.complicacion) "Sí" else "No")
                    if (monitoreoReal.complicacion) {
                        LabeledBlock("Detalle Complicación", monitoreoReal.detalleComplicacion)
                    }
                    LabeledBlock("Cambio de Motor", monitoreoReal.cambioMotor)
                }
            }
        }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text("¿Eliminar monitoreo?") },
                text = { Text("Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        monitoreoReal?.let {
                            monitoreoViewModel.deleteMonitoreo(it)
                            scope.launch {
                                snackBarHostState.showSnackbar("Monitoreo eliminado correctamente")
                            }
                            onBack()
                        }
                        openDialog.value = false
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showEditWarningDialog.value) {
            AlertDialog(
                onDismissRequest = { showEditWarningDialog.value = false },
                title = { Text("Editar monitoreo con datos eliminados") },
                text = {
                    Text("Este monitoreo contiene información de entidades que ya no existen. Al editarlo, deberás volver a seleccionar esos valores o se perderán.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        showEditWarningDialog.value = false
                        monitoreoReal?.let(onEdit)
                    }) {
                        Text("Continuar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEditWarningDialog.value = false
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
@Composable
fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.primary)
        Text(value, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun LabeledBlock(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(
                text = value.ifBlank { "Sin información" },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
