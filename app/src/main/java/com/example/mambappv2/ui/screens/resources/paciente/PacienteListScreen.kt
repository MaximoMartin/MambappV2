// PacienteListScreen.kt
package com.example.mambappv2.ui.screens.resources.paciente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mambappv2.data.entities.Paciente
import com.example.mambappv2.ui.components.dialogs.SimpleInputDialog
import com.example.mambappv2.viewmodel.PacienteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteListScreen(
    navController: NavController,
    viewModel: PacienteViewModel
) {
    val pacientes by viewModel.pacientes.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val editingPaciente = remember { mutableStateOf<Paciente?>(null) }

    val showConfirmDelete = remember { mutableStateOf(false) }
    val pacienteToDelete = remember { mutableStateOf<Paciente?>(null) }

    val campos = remember {
        mutableStateListOf(
            "DNI" to mutableStateOf(""),
            "Nombre" to mutableStateOf(""),
            "Apellido" to mutableStateOf(""),
            "Edad" to mutableStateOf(""),
            "Mutual" to mutableStateOf("")
        )
    }

    fun resetCampos(p: Paciente? = null) {
        campos[0].second.value = p?.dniPaciente?.toString() ?: ""
        campos[1].second.value = p?.nombre ?: ""
        campos[2].second.value = p?.apellido ?: ""
        campos[3].second.value = p?.edad?.toString() ?: ""
        campos[4].second.value = p?.mutual ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Pacientes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingPaciente.value = null
                resetCampos()
                showDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Paciente")
            }
        }
    ) { padding ->
        if (pacientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pacientes creados aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pacientes) { paciente ->
                    PacienteItem(
                        paciente = paciente,
                        onEdit = {
                            editingPaciente.value = paciente
                            resetCampos(paciente)
                            showDialog.value = true
                        },
                        onDelete = {
                            pacienteToDelete.value = paciente
                            showConfirmDelete.value = true
                        }
                    )
                }
            }
        }

        if (showDialog.value) {
            SimpleInputDialog(
                title = if (editingPaciente.value == null) "Nuevo Paciente" else "Editar Paciente",
                fields = campos,
                onDismiss = { showDialog.value = false },
                onConfirm = {
                    val dni = campos[0].second.value.toIntOrNull() ?: return@SimpleInputDialog
                    val nombre = campos[1].second.value
                    val apellido = campos[2].second.value
                    val edad = campos[3].second.value.toIntOrNull() ?: 0
                    val mutual = campos[4].second.value

                    if (editingPaciente.value == null) {
                        viewModel.addPaciente(dni, nombre, apellido, edad, mutual)
                    } else {
                        viewModel.updatePaciente(
                            editingPaciente.value!!.copy(
                                dniPaciente = dni,
                                nombre = nombre,
                                apellido = apellido,
                                edad = edad,
                                mutual = mutual
                            )
                        )
                    }
                    showDialog.value = false
                }
            )
        }

        if (showConfirmDelete.value && pacienteToDelete.value != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar paciente?") },
                text = { Text("Esta acción no se puede deshacer y afectara a los registros creados.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            pacienteToDelete.value?.let { viewModel.deletePaciente(it) }
                            showConfirmDelete.value = false
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDelete.value = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun PacienteItem(
    paciente: Paciente,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${paciente.nombre} ${paciente.apellido}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("DNI: ${paciente.dniPaciente}")
            Text("Edad: ${paciente.edad}")
            Text("Mutual: ${paciente.mutual}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onEdit) {
                    Text("Editar")
                }
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}
