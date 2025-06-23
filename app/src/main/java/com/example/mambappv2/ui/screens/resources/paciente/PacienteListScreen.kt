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
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.ui.components.dialogs.SimpleInputDialog
import com.example.mambappv2.viewmodel.PacienteViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteListScreen(
    navController: NavController,
    viewModel: PacienteViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val pacientes by viewModel.pacientes.collectAsState()
    val pacienteUsageCount by monitoreoViewModel.pacienteUsageCount.collectAsState()
    
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
                    val usageCount = pacienteUsageCount[paciente.dniPaciente] ?: 0
                    
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
        }

        if (showDialog.value) {
            SimpleInputDialog(
                title = if (editingPaciente.value != null) "Editar Paciente" else "Nuevo Paciente",
                fields = campos,
                onDismiss = { showDialog.value = false },
                onConfirm = {
                    val dniText = campos[0].second.value
                    val nombre = campos[1].second.value
                    val apellido = campos[2].second.value
                    val edadText = campos[3].second.value
                    val mutual = campos[4].second.value

                    if (dniText.isNotBlank() && nombre.isNotBlank() && apellido.isNotBlank() && edadText.isNotBlank()) {
                        try {
                            val dni = dniText.toInt()
                            val edad = edadText.toInt()
                            val actual = editingPaciente.value

                            if (actual != null) {
                                viewModel.updatePaciente(
                                    actual.copy(
                                        dniPaciente = dni,
                                        nombre = nombre,
                                        apellido = apellido,
                                        edad = edad,
                                        mutual = mutual
                                    )
                                )
                            } else {
                                viewModel.addPaciente(dni, nombre, apellido, edad, mutual)
                            }
                            showDialog.value = false
                        } catch (e: NumberFormatException) {
                            // Mantener diálogo abierto si hay error de formato
                        }
                    }
                }
            )
        }

        if (showConfirmDelete.value && pacienteToDelete.value != null) {
            val paciente = pacienteToDelete.value!!
            val currentUsageCount = pacienteUsageCount[paciente.dniPaciente] ?: 0
            
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar paciente?") },
                text = {
                    if (currentUsageCount > 0) {
                        Text("No se puede eliminar este paciente porque está siendo usado en $currentUsageCount monitoreo(s).")
                    } else {
                        Text("Esta acción no se puede deshacer.")
                    }
                },
                confirmButton = {
                    if (currentUsageCount == 0) {
                        TextButton(
                            onClick = {
                                viewModel.deletePaciente(paciente)
                                showConfirmDelete.value = false
                            }
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDelete.value = false }) {
                        Text(if (currentUsageCount > 0) "Entendido" else "Cancelar")
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
