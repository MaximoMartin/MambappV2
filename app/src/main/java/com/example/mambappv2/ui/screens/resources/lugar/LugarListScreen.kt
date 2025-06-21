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
import com.example.mambappv2.viewmodel.LugarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LugarListScreen(
    navController: NavController,
    viewModel: LugarViewModel
) {
    val lugares by viewModel.lugares.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val editingLugar = remember { mutableStateOf<Lugar?>(null) }

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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${lugar.nombre}, ${lugar.provincia}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    editingLugar.value = lugar
                                    resetCampos(lugar)
                                    showDialog.value = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.deleteLugar(lugar) }) {
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
                    Text(if (editingLugar.value != null) "Editar Lugar" else "Nuevo Lugar")
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del lugar") },
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
                            val lugar = editingLugar.value
                            if (lugar != null) {
                                viewModel.updateLugar(lugar.copy(nombre = nombre.text, provincia = provincia.text))
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
    }
}
