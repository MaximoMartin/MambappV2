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
import com.example.mambappv2.viewmodel.SolicitanteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitanteListScreen(
    navController: NavController,
    viewModel: SolicitanteViewModel
) {
    val solicitantes by viewModel.solicitantes.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val editing = remember { mutableStateOf<Solicitante?>(null) }

    var nombre by remember { mutableStateOf(TextFieldValue()) }
    var apellido by remember { mutableStateOf(TextFieldValue()) }

    fun resetCampos(s: Solicitante? = null) {
        nombre = TextFieldValue(s?.nombre ?: "")
        apellido = TextFieldValue(s?.apellido ?: "")
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${solicitante.nombre} ${solicitante.apellido}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    editing.value = solicitante
                                    resetCampos(solicitante)
                                    showDialog.value = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.deleteSolicitante(solicitante) }) {
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
                            val s = editing.value
                            if (s != null) {
                                viewModel.updateSolicitante(s.copy(nombre = nombre.text, apellido = apellido.text))
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
    }
}
