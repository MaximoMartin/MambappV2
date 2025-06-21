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
import androidx.navigation.NavController
import com.example.mambappv2.navigation.NavigationRoutes
import com.example.mambappv2.ui.screens.resources.ResourceType.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceMenuScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Recursos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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

            ResourceMenuItem(Paciente) { navController.navigate(NavigationRoutes.PacienteList.route) }
            ResourceMenuItem(Medico) { navController.navigate(NavigationRoutes.MedicoList.route) }
            ResourceMenuItem(Tecnico) { navController.navigate(NavigationRoutes.TecnicoList.route) }
            ResourceMenuItem(Solicitante) { navController.navigate(NavigationRoutes.SolicitanteList.route) }
            ResourceMenuItem(Lugar) { navController.navigate(NavigationRoutes.LugarList.route) }
            ResourceMenuItem(Patologia) { navController.navigate(NavigationRoutes.PatologiaList.route) }
            ResourceMenuItem(Equipo) { navController.navigate(NavigationRoutes.EquipoList.route) }
        }
    }
}

@Composable
fun ResourceMenuItem(
    type: ResourceType,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
