// PatologiaListScreen.kt
package com.example.mambappv2.ui.screens.resources.patologia

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
import com.example.mambappv2.data.entities.Patologia
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.viewmodel.PatologiaViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatologiaListScreen(
    navController: NavController,
    viewModel: PatologiaViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val patologias by viewModel.patologias.collectAsState()
    val patologiaUsageCount by monitoreoViewModel.patologiaUsageCount.collectAsState()
    
    val showDialog = remember { mutableStateOf(false) }
    val editingPatologia = remember { mutableStateOf<Patologia?>(null) }

    val showConfirmDelete = remember { mutableStateOf(false) }
    val patologiaToDelete = remember { mutableStateOf<Patologia?>(null) }

    var nombre by remember { mutableStateOf(TextFieldValue()) }

    fun resetCampos(patologia: Patologia? = null) {
        nombre = TextFieldValue(patologia?.nombre ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Patologías") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingPatologia.value = null
                resetCampos()
                showDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Patología")
            }
        }
    ) { padding ->
        if (patologias.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay patologías creadas aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(patologias) { patologia ->
                    val usageCount = patologiaUsageCount[patologia.id] ?: 0
                    
                    ResourceCard(
                        title = patologia.nombre,
                        subtitle = "ID: ${patologia.id}",
                        usageCount = usageCount,
                        onEdit = {
                            editingPatologia.value = patologia
                            resetCampos(patologia)
                            showDialog.value = true
                        },
                        onDelete = {
                            if (usageCount == 0) {
                                patologiaToDelete.value = patologia
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
                    Text(if (editingPatologia.value != null) "Editar Patología" else "Nueva Patología")
                },
                text = {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (nombre.text.isNotBlank()) {
                            val patologia = editingPatologia.value
                            if (patologia != null) {
                                viewModel.updatePatologia(patologia.copy(nombre = nombre.text))
                            } else {
                                viewModel.addPatologia(nombre.text)
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

        if (showConfirmDelete.value && patologiaToDelete.value != null) {
            val patologia = patologiaToDelete.value!!
            val currentUsageCount = patologiaUsageCount[patologia.id] ?: 0
            
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar patología?") },
                text = {
                    if (currentUsageCount > 0) {
                        Text("No se puede eliminar esta patología porque está siendo usada en $currentUsageCount monitoreo(s).")
                    } else {
                        Text("Esta acción no se puede deshacer.")
                    }
                },
                confirmButton = {
                    if (currentUsageCount == 0) {
                        TextButton(
                            onClick = {
                                viewModel.deletePatologia(patologia)
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
