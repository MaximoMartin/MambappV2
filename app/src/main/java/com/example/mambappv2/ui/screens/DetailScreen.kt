// DetailScreen.kt
package com.example.mambappv2.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.viewmodel.*
import kotlinx.coroutines.launch

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
    val lugares by lugarViewModel.lugares.collectAsState()
    val patologias by patologiaViewModel.patologias.collectAsState()
    val medicos by medicoViewModel.medicos.collectAsState()
    val tecnicos by tecnicoViewModel.tecnicos.collectAsState()
    val solicitantes by solicitanteViewModel.solicitantes.collectAsState()
    val pacientes by pacienteViewModel.pacientes.collectAsState()
    val equipos by equipoViewModel.equipos.collectAsState()


    val equipo = equipos.find { it.id == monitoreo?.idEquipo }?.let { "Equipo Nº${it.numero} — ${it.descripcion}" }
        ?: monitoreo?.equipoSnapshot ?: "No asignado"

    val paciente = pacientes.find { it.dniPaciente == monitoreo?.dniPaciente }

    val lugar = lugares.find { it.id == monitoreo?.idLugar }?.let { "${it.nombre}, ${it.provincia}" }
        ?: monitoreo?.lugarSnapshot ?: "Desconocido"

    val patologia = patologias.find { it.id == monitoreo?.idPatologia }?.nombre
        ?: monitoreo?.patologiaSnapshot ?: "Desconocida"

    val medico = medicos.find { it.id == monitoreo?.idMedico }?.let { "${it.nombre} ${it.apellido}" }
        ?: monitoreo?.medicoSnapshot ?: "No asignado"

    val tecnico = tecnicos.find { it.id == monitoreo?.idTecnico }?.let { "${it.nombre} ${it.apellido}" }
        ?: monitoreo?.tecnicoSnapshot ?: "No asignado"

    val solicitante = solicitantes.find { it.id == monitoreo?.idSolicitante }?.let { "${it.nombre} ${it.apellido}" }
        ?: monitoreo?.solicitanteSnapshot ?: "No asignado"


    val openDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val entidadFaltante = monitoreo != null && listOf(
        medicos.any { it.id == monitoreo.idMedico },
        tecnicos.any { it.id == monitoreo.idTecnico },
        lugares.any { it.id == monitoreo.idLugar },
        patologias.any { it.id == monitoreo.idPatologia },
        solicitantes.any { it.id == monitoreo.idSolicitante },
        equipos.any { it.id == monitoreo.idEquipo || monitoreo.idEquipo == null },
        pacientes.any { it.dniPaciente == monitoreo.dniPaciente }
    ).contains(false)

    val showEditWarningDialog = remember { mutableStateOf(false) }


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
                            monitoreo?.let(onEdit)
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
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) { padding ->
        if (monitoreo == null) {
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
                DetailCard(title = "🧾 Registro #${monitoreo.nroRegistro}") {
                    InfoLine("Fecha Realizado", monitoreo.fechaRealizado)
                    InfoLine("Fecha Presentado", monitoreo.fechaPresentado ?: "No informada")
                    InfoLine("Fecha Cobrado", monitoreo.fechaCobrado ?: "No informada")
                }

                DetailCard(title = "👤 Paciente") {
                    if (paciente != null) {
                        InfoLine("DNI", paciente.dniPaciente.toString())
                        InfoLine("Nombre", paciente.nombre)
                        InfoLine("Apellido", paciente.apellido)
                        InfoLine("Edad", paciente.edad.toString())
                        InfoLine("Mutual", paciente.mutual)
                    } else {
                        InfoLine("DNI", monitoreo?.dniPaciente?.toString() ?: "-")
                        InfoLine("Nombre", monitoreo?.pacienteNombre ?: "Desconocido")
                        InfoLine("Apellido", monitoreo?.pacienteApellido ?: "Desconocido")
                        InfoLine("Edad", monitoreo?.pacienteEdad?.toString() ?: "-")
                        InfoLine("Mutual", monitoreo?.pacienteMutual ?: "-")
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
                    LabeledBlock(label = "Anestesia", value = monitoreo.detalleAnestesia)

                    InfoLine("Complicación", if (monitoreo.complicacion) "Sí" else "No")

                    if (monitoreo.complicacion) {
                        LabeledBlock(label = "Detalle Complicación", value = monitoreo.detalleComplicacion)
                    }

                    LabeledBlock(label = "Cambio de Motor", value = monitoreo.cambioMotor)
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
                        monitoreo?.let {
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
                    Text("Este monitoreo contiene información de entidades que ya no existen (como médico, técnico, etc.). Al editarlo, deberás volver a seleccionar esos valores o se perderán los datos originales.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        showEditWarningDialog.value = false
                        monitoreo?.let(onEdit)
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
