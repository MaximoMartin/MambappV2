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
import com.example.mambappv2.viewmodel.PatologiaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatologiaListScreen(
    navController: NavController,
    viewModel: PatologiaViewModel
) {
    val patologias by viewModel.patologias.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val editingPatologia = remember { mutableStateOf<Patologia?>(null) }

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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(patologia.nombre, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    editingPatologia.value = patologia
                                    resetCampos(patologia)
                                    showDialog.value = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.deletePatologia(patologia) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
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
    }
}
