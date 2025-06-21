// ListScreen.kt
package com.example.mambappv2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mambappv2.viewmodel.MonitoreoViewModel
import com.example.mambappv2.viewmodel.LugarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: MonitoreoViewModel,
    lugarViewModel: LugarViewModel,
    navController: NavController
) {
    val monitoreos by viewModel.monitoreos.collectAsState()
    val lugares by lugarViewModel.lugares.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📋 Registros Guardados") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (monitoreos.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("📭 No hay monitoreos guardados aún")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                itemsIndexed(monitoreos) { _, monitoreo ->
                    val lugarNombre = lugares.find { it.id == monitoreo.idLugar }?.nombre ?: "Lugar desconocido"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("monitoreo", monitoreo)
                                navController.navigate("detail")
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📄 Registro #${monitoreo.nroRegistro} • DNI: ${monitoreo.dniPaciente}")
                            Text("🗓️ Fecha: ${monitoreo.fechaRealizado}")
                            Text("📍 Lugar: $lugarNombre")
                        }
                    }
                }
            }
        }
    }
}
