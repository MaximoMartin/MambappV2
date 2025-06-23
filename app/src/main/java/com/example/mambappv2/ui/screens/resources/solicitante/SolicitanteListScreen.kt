// SolicitanteListScreen.kt
package com.example.mambappv2.ui.screens.resources.solicitante

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
import com.example.mambappv2.data.entities.Solicitante
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.viewmodel.SolicitanteViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitanteListScreen(
    navController: NavController,
    viewModel: SolicitanteViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val solicitantes by viewModel.solicitantes.collectAsState()
    val solicitanteUsageCount by monitoreoViewModel.solicitanteUsageCount.collectAsState()
    
    val showDialog = remember { mutableStateOf(false) }
    val editing = remember { mutableStateOf<Solicitante?>(null) }
    val showConfirmDelete = remember { mutableStateOf(false) }
    val solicitanteToDelete = remember { mutableStateOf<Solicitante?>(null) }

    var nombre by remember { mutableStateOf(TextFieldValue()) }
    var apellido by remember { mutableStateOf(TextFieldValue()) }

    fun resetCampos(solicitante: Solicitante? = null) {
        nombre = TextFieldValue(solicitante?.nombre ?: "")
        apellido = TextFieldValue(solicitante?.apellido ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Solicitantes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editing.value = null
                resetCampos()
                showDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Solicitante")
            }
        }
    ) { padding ->
        if (solicitantes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay solicitantes creados aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(solicitantes) { solicitante ->
                    val usageCount = solicitanteUsageCount[solicitante.id] ?: 0
                    
                    ResourceCard(
                        title = "${solicitante.nombre} ${solicitante.apellido}",
                        subtitle = "ID: ${solicitante.id}",
                        usageCount = usageCount,
                        onEdit = {
                            editing.value = solicitante
                            resetCampos(solicitante)
                            showDialog.value = true
                        },
                        onDelete = {
                            if (usageCount == 0) {
                                solicitanteToDelete.value = solicitante
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
                    Text(if (editing.value != null) "Editar Solicitante" else "Nuevo Solicitante")
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
                            val actual = editing.value
                            if (actual != null) {
                                viewModel.updateSolicitante(actual.copy(nombre = nombre.text, apellido = apellido.text))
                            } else {
                                viewModel.addSolicitante(nombre.text, apellido.text)
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

        if (showConfirmDelete.value && solicitanteToDelete.value != null) {
            val solicitante = solicitanteToDelete.value!!
            val currentUsageCount = solicitanteUsageCount[solicitante.id] ?: 0
            
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar solicitante?") },
                text = {
                    if (currentUsageCount > 0) {
                        Text("No se puede eliminar este solicitante porque está siendo usado en $currentUsageCount monitoreo(s).")
                    } else {
                        Text("Esta acción no se puede deshacer.")
                    }
                },
                confirmButton = {
                    if (currentUsageCount == 0) {
                        TextButton(
                            onClick = {
                                viewModel.deleteSolicitante(solicitante)
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
