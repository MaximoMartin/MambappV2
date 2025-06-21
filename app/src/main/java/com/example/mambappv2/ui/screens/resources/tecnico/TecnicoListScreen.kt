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
import com.example.mambappv2.viewmodel.TecnicoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecnicoListScreen(
    navController: NavController,
    viewModel: TecnicoViewModel
) {
    val tecnicos by viewModel.tecnicos.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val editingTecnico = remember { mutableStateOf<Tecnico?>(null) }

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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${tecnico.nombre} ${tecnico.apellido}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    editingTecnico.value = tecnico
                                    resetCampos(tecnico)
                                    showDialog.value = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.deleteTecnico(tecnico) }) {
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
    }
}
