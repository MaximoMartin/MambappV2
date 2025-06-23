// MedicoListScreen.kt
package com.example.mambappv2.ui.screens.resources.medico

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mambappv2.data.entities.Medico
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.viewmodel.MedicoViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicoListScreen(
    navController: NavController,
    viewModel: MedicoViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val medicos by viewModel.medicos.collectAsState()
    val medicoUsageCount by monitoreoViewModel.medicoUsageCount.collectAsState()
    
    val showDialog = remember { mutableStateOf(false) }
    val editingMedico = remember { mutableStateOf<Medico?>(null) }

    val showConfirmDelete = remember { mutableStateOf(false) }
    val medicoToDelete = remember { mutableStateOf<Medico?>(null) }

    var nombre by remember { mutableStateOf(TextFieldValue()) }
    var apellido by remember { mutableStateOf(TextFieldValue()) }

    fun resetCampos(medico: Medico? = null) {
        nombre = TextFieldValue(medico?.nombre ?: "")
        apellido = TextFieldValue(medico?.apellido ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Médicos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingMedico.value = null
                resetCampos()
                showDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Médico")
            }
        }
    ) { padding ->
        if (medicos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay médicos creados aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medicos) { medico ->
                    val usageCount = medicoUsageCount[medico.id] ?: 0
                    
                    ResourceCard(
                        title = "${medico.nombre} ${medico.apellido}",
                        subtitle = "ID: ${medico.id}",
                        usageCount = usageCount,
                        onEdit = {
                            editingMedico.value = medico
                            resetCampos(medico)
                            showDialog.value = true
                        },
                        onDelete = {
                            if (usageCount == 0) {
                                medicoToDelete.value = medico
                                showConfirmDelete.value = true
                            }
                        }
                    )
                }
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(if (editingMedico.value != null) "Editar Médico" else "Nuevo Médico")
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = apellido,
                            onValueChange = { apellido = it },
                            label = { Text("Apellido") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (nombre.text.isNotBlank() && apellido.text.isNotBlank()) {
                            val actual = editingMedico.value
                            if (actual != null) {
                                viewModel.updateMedico(actual.copy(nombre = nombre.text, apellido = apellido.text))
                            } else {
                                viewModel.addMedico(nombre.text, apellido.text)
                            }
                            showDialog.value = false
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showConfirmDelete.value && medicoToDelete.value != null) {
            val medico = medicoToDelete.value!!
            val currentUsageCount = medicoUsageCount[medico.id] ?: 0
            
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar médico?") },
                text = {
                    if (currentUsageCount > 0) {
                        Text("No se puede eliminar este médico porque está siendo usado en $currentUsageCount monitoreo(s).")
                    } else {
                        Text("Esta acción no se puede deshacer.")
                    }
                },
                confirmButton = {
                    if (currentUsageCount == 0) {
                        TextButton(
                            onClick = {
                                viewModel.deleteMedico(medico)
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
