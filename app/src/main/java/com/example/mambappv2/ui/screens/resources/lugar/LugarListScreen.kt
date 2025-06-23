// LugarListScreen.kt
package com.example.mambappv2.ui.screens.resources.lugar

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
import com.example.mambappv2.data.entities.Lugar
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.viewmodel.LugarViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LugarListScreen(
    navController: NavController,
    viewModel: LugarViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val lugares by viewModel.lugares.collectAsState()
    val lugarUsageCount by monitoreoViewModel.lugarUsageCount.collectAsState()
    
    val showDialog = remember { mutableStateOf(false) }
    val editingLugar = remember { mutableStateOf<Lugar?>(null) }
    val showConfirmDelete = remember { mutableStateOf(false) }
    val lugarToDelete = remember { mutableStateOf<Lugar?>(null) }

    var nombre by remember { mutableStateOf(TextFieldValue()) }
    var provincia by remember { mutableStateOf(TextFieldValue()) }

    fun resetCampos(lugar: Lugar? = null) {
        nombre = TextFieldValue(lugar?.nombre ?: "")
        provincia = TextFieldValue(lugar?.provincia ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Lugares") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingLugar.value = null
                resetCampos()
                showDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Lugar")
            }
        }
    ) { padding ->
        if (lugares.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay lugares creados aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lugares) { lugar ->
                    val usageCount = lugarUsageCount[lugar.id] ?: 0
                    
                    ResourceCard(
                        title = "${lugar.nombre}, ${lugar.provincia}",
                        subtitle = "ID: ${lugar.id}",
                        usageCount = usageCount,
                        onEdit = {
                            editingLugar.value = lugar
                            resetCampos(lugar)
                            showDialog.value = true
                        },
                        onDelete = {
                            if (usageCount == 0) {
                                lugarToDelete.value = lugar
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
                    Text(if (editingLugar.value != null) "Editar Lugar" else "Nuevo Lugar")
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
                            value = provincia,
                            onValueChange = { provincia = it },
                            label = { Text("Provincia") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (nombre.text.isNotBlank() && provincia.text.isNotBlank()) {
                            val actual = editingLugar.value
                            if (actual != null) {
                                viewModel.updateLugar(actual.copy(nombre = nombre.text, provincia = provincia.text))
                            } else {
                                viewModel.addLugar(nombre.text, provincia.text)
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

        if (showConfirmDelete.value && lugarToDelete.value != null) {
            val lugar = lugarToDelete.value!!
            val currentUsageCount = lugarUsageCount[lugar.id] ?: 0
            
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar lugar?") },
                text = {
                    if (currentUsageCount > 0) {
                        Text("No se puede eliminar este lugar porque está siendo usado en $currentUsageCount monitoreo(s).")
                    } else {
                        Text("Esta acción no se puede deshacer.")
                    }
                },
                confirmButton = {
                    if (currentUsageCount == 0) {
                        TextButton(
                            onClick = {
                                viewModel.deleteLugar(lugar)
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
