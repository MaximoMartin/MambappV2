// DetailScreen.kt
package com.example.mambappv2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.ui.components.SectionHeader
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    monitoreo: Monitoreo?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Monitoreo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (monitoreo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("❌ No se encontró ningún monitoreo", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🗓️ Fecha realizado: ${monitoreo.fechaRealizado}")
                Text("👤 DNI paciente: ${monitoreo.dniPaciente}")
                Text("📍 Lugar ID: ${monitoreo.idLugar}")
                Text("🧬 Patología ID: ${monitoreo.idPatologia}")
                Text("🩺 Médico ID: ${monitoreo.idMedico}")
                Text("🔧 Técnico ID: ${monitoreo.idTecnico}")
                Text("📝 Anestesia: ${monitoreo.detalleAnestesia}")
                if (monitoreo.complicacion) {
                    Text("⚠️ Complicación: ${monitoreo.detalleComplicacion}")
                }
                Text("⚙️ Cambio motor: ${monitoreo.cambioMotor}")
            }
        }
    }
}

@Composable
fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, color = Color.Black)
    }
}
