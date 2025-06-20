package com.example.mambappv2.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.ui.components.dialogs.SimpleInputDialog
import com.example.mambappv2.ui.components.monitoreo.FechaSection
import com.example.mambappv2.ui.components.monitoreo.PacienteSection
import com.example.mambappv2.viewmodel.*
import com.example.mambappv2.ui.components.sections.DetalleClinicoSection
import com.example.mambappv2.ui.components.sections.LugarSection
import com.example.mambappv2.ui.components.sections.PatologiaSection
import com.example.mambappv2.ui.components.sections.ProfesionalesSection
import com.example.mambappv2.ui.state.MonitoreoFormState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val formState = remember { MonitoreoFormState() }

    val pacientes by pacienteViewModel.pacientes.collectAsState()
    val paciente = pacientes.find { it.dniPaciente.toString() == formState.dniPaciente }

    val medicos by medicoViewModel.medicos.collectAsState()
    val tecnicos by tecnicoViewModel.tecnicos.collectAsState()
    val solicitantes by solicitanteViewModel.solicitantes.collectAsState()
    val lugares by lugarViewModel.lugares.collectAsState()
    val patologias by patologiaViewModel.patologias.collectAsState()


    //Persistencia
    fun saveMonitoreo() {
        val hoy = LocalDate.now()

        // Validar fechas
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

        // Verificar existencia del paciente
        val pacienteExiste = pacientes.any { it.dniPaciente.toString() == formState.dniPaciente }
        if (!pacienteExiste) {
            Toast.makeText(context, "Debes agregar un paciente válido antes de guardar", Toast.LENGTH_LONG).show()
            return
        }

        // Generar nuevo nroRegistro
        val nroNuevo = viewModel.monitoreos.value.maxOfOrNull { it.nroRegistro }?.plus(1) ?: 1

        val nuevo = Monitoreo(
            nroRegistro = nroNuevo,
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
            cambioMotor = formState.cambioMotor
        )

        viewModel.addMonitoreo(nuevo)
        onSaveSuccess()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Monitoreo", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton( onClick = { saveMonitoreo() }) {
                Icon(Icons.Default.Check, contentDescription = "Guardar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FechaSection(
                fechaRealizado = formState.fechaRealizado,
                onFechaRealizadoChange = { formState.fechaRealizado = it },
                fechaPresentado = formState.fechaPresentado,
                onFechaPresentadoChange = { formState.fechaPresentado = it },
                fechaCobrado = formState.fechaCobrado,
                onFechaCobradoChange = { formState.fechaCobrado = it }
            )

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

            LugarSection(
                lugares = lugares,
                selectedLugarId = formState.selectedLugarId,
                onLugarSelected = { formState.selectedLugarId = it },
                onAddLugar = {
                    formState.showDialogTipo = "Lugar"
                    formState.setCamposDialog("Nombre", "Provincia")
                }
            )

            PatologiaSection(
                patologias = patologias,
                selectedPatologiaId = formState.selectedPatologiaId,
                onPatologiaSelected = { formState.selectedPatologiaId = it },
                onAddPatologia = {
                    formState.showDialogTipo = "Patología"
                    formState.setCamposDialog("Nombre")
                }
            )

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
                    }
                    formState.showDialogTipo = ""
                }
            )
        }
    }
}
