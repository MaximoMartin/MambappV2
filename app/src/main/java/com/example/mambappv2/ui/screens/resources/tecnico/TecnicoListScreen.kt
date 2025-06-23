// TecnicoListScreen.kt
package com.example.mambappv2.ui.screens.resources.tecnico

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
import com.example.mambappv2.data.entities.Tecnico
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.viewmodel.TecnicoViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecnicoListScreen(
    navController: NavController,
    viewModel: TecnicoViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val tecnicos by viewModel.tecnicos.collectAsState()
    val tecnicoUsageCount by monitoreoViewModel.tecnicoUsageCount.collectAsState()
    
    val showDialog = remember { mutableStateOf(false) }
    val editingTecnico = remember { mutableStateOf<Tecnico?>(null) }

    val showConfirmDelete = remember { mutableStateOf(false) }
    val tecnicoToDelete = remember { mutableStateOf<Tecnico?>(null) }

    var nombre by remember { mutableStateOf(TextFieldValue()) }
    var apellido by remember { mutableStateOf(TextFieldValue()) }

    fun resetCampos(tecnico: Tecnico? = null) {
        nombre = TextFieldValue(tecnico?.nombre ?: "")
        apellido = TextFieldValue(tecnico?.apellido ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Técnicos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingTecnico.value = null
                resetCampos()
                showDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Técnico")
            }
        }
    ) { padding ->
        if (tecnicos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay técnicos creados aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tecnicos) { tecnico ->
                    val usageCount = tecnicoUsageCount[tecnico.id] ?: 0
                    
                    ResourceCard(
                        title = "${tecnico.nombre} ${tecnico.apellido}",
                        subtitle = "ID: ${tecnico.id}",
                        usageCount = usageCount,
                        onEdit = {
                            editingTecnico.value = tecnico
                            resetCampos(tecnico)
                            showDialog.value = true
                        },
                        onDelete = {
                            if (usageCount == 0) {
                                tecnicoToDelete.value = tecnico
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
                    Text(if (editingTecnico.value != null) "Editar Técnico" else "Nuevo Técnico")
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
                            val actual = editingTecnico.value
                            if (actual != null) {
                                viewModel.updateTecnico(actual.copy(nombre = nombre.text, apellido = apellido.text))
                            } else {
                                viewModel.addTecnico(nombre.text, apellido.text)
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

        if (showConfirmDelete.value && tecnicoToDelete.value != null) {
            val tecnico = tecnicoToDelete.value!!
            val currentUsageCount = tecnicoUsageCount[tecnico.id] ?: 0
            
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar técnico?") },
                text = {
                    if (currentUsageCount > 0) {
                        Text("No se puede eliminar este técnico porque está siendo usado en $currentUsageCount monitoreo(s).")
                    } else {
                        Text("Esta acción no se puede deshacer.")
                    }
                },
                confirmButton = {
                    if (currentUsageCount == 0) {
                        TextButton(
                            onClick = {
                                viewModel.deleteTecnico(tecnico)
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
