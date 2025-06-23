// EquipoListScreen.kt
package com.example.mambappv2.ui.screens.resources.equipo

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
import com.example.mambappv2.data.entities.Equipo
import com.example.mambappv2.ui.components.ResourceCard
import com.example.mambappv2.viewmodel.EquipoViewModel
import com.example.mambappv2.viewmodel.MonitoreoViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoListScreen(
    navController: NavController,
    viewModel: EquipoViewModel,
    monitoreoViewModel: MonitoreoViewModel
) {
    val equipos by viewModel.equipos.collectAsState()
    val equipoUsageCount by monitoreoViewModel.equipoUsageCount.collectAsState()
    
    val showDialog = remember { mutableStateOf(false) }
    val editingEquipo = remember { mutableStateOf<Equipo?>(null) }

    val showConfirmDelete = remember { mutableStateOf(false) }
    val equipoToDelete = remember { mutableStateOf<Equipo?>(null) }

    var numero by remember { mutableStateOf(TextFieldValue()) }
    var descripcion by remember { mutableStateOf(TextFieldValue()) }

    fun resetCampos(equipo: Equipo? = null) {
        numero = TextFieldValue(equipo?.numero ?: "")
        descripcion = TextFieldValue(equipo?.descripcion ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Equipos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingEquipo.value = null
                resetCampos()
                showDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Equipo")
            }
        }
    ) { padding ->
        if (equipos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay equipos creados aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(equipos) { equipo ->
                    val usageCount = equipoUsageCount[equipo.id] ?: 0
                    
                    ResourceCard(
                        title = "Equipo N°: ${equipo.numero}",
                        subtitle = if (equipo.descripcion.isNotBlank()) 
                            equipo.descripcion 
                        else 
                            "ID: ${equipo.id}",
                        usageCount = usageCount,
                        onEdit = {
                            editingEquipo.value = equipo
                            resetCampos(equipo)
                            showDialog.value = true
                        },
                        onDelete = {
                            if (usageCount == 0) {
                                equipoToDelete.value = equipo
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
                    Text(if (editingEquipo.value != null) "Editar Equipo" else "Nuevo Equipo")
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = numero,
                            onValueChange = { numero = it },
                            label = { Text("Número de Equipo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción (opcional)") },
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (numero.text.isNotBlank()) {
                            val equipo = editingEquipo.value
                            if (equipo != null) {
                                viewModel.updateEquipo(equipo.copy(
                                    numero = numero.text,
                                    descripcion = descripcion.text
                                ))
                            } else {
                                viewModel.addEquipo(numero.text, descripcion.text)
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

        if (showConfirmDelete.value && equipoToDelete.value != null) {
            val equipo = equipoToDelete.value!!
            val currentUsageCount = equipoUsageCount[equipo.id] ?: 0
            
            AlertDialog(
                onDismissRequest = { showConfirmDelete.value = false },
                title = { Text("¿Eliminar equipo?") },
                text = {
                    if (currentUsageCount > 0) {
                        Text("No se puede eliminar este equipo porque está siendo usado en $currentUsageCount monitoreo(s).")
                    } else {
                        Text("Esta acción no se puede deshacer.")
                    }
                },
                confirmButton = {
                    if (currentUsageCount == 0) {
                        TextButton(
                            onClick = {
                                viewModel.deleteEquipo(equipo)
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
