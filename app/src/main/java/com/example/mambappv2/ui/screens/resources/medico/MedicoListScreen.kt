package com.example.mambappv2.ui.screens.resources.medico

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mambappv2.data.entities.Medico
import com.example.mambappv2.viewmodel.MedicoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicoListScreen(
    navController: NavController,
    viewModel: MedicoViewModel
) {
    val medicos by viewModel.medicos.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val editingMedico = remember { mutableStateOf<Medico?>(null) }

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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${medico.nombre} ${medico.apellido}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    editingMedico.value = medico
                                    resetCampos(medico)
                                    showDialog.value = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.deleteMedico(medico) }) {
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
    }
}
