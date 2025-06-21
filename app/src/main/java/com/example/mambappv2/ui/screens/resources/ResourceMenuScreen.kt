package com.example.mambappv2.ui.screens.resources

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mambappv2.ui.screens.resources.ResourceType.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceMenuScreen(
    onBack: () -> Unit,
    onNavigateTo: (ResourceType) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Recursos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Seleccioná una entidad para gestionar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            ResourceMenuItem(Paciente, onNavigateTo)
            ResourceMenuItem(Medico, onNavigateTo)
            ResourceMenuItem(Tecnico, onNavigateTo)
            ResourceMenuItem(Solicitante, onNavigateTo)
            ResourceMenuItem(Lugar, onNavigateTo)
            ResourceMenuItem(Patologia, onNavigateTo)
            ResourceMenuItem(Equipo, onNavigateTo)
        }
    }
}

@Composable
fun ResourceMenuItem(
    type: ResourceType,
    onClick: (ResourceType) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(type) }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = type.label,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = type.label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
