package com.example.mambappv2.ui.screens.resources

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mambappv2.navigation.NavigationRoutes
import com.example.mambappv2.ui.screens.resources.ResourceType.*
import com.example.mambappv2.ui.theme.*
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceMenuScreen(
    navController: NavController
) {
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    // Animación de ondas
    val infiniteTransition = rememberInfiniteTransition(label = "resources_animation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo animado
        ResourcesBackground(isDarkMode = isDarkMode, waveOffset = waveOffset)
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ModernTopAppBar(
                    title = "Gestión de Recursos",
                    onBackClick = { navController.popBackStack() }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Header section
                ResourcesHeader()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Lista de recursos con cards modernas
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(getResourceTypes()) { index, resourceType ->
                        ModernResourceCard(
                            type = resourceType,
                            index = index,
                            onClick = { navigateToResource(navController, resourceType) }
                        )
                    }
                    
                    // Spacer final
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ResourcesBackground(
    isDarkMode: Boolean,
    waveOffset: Float
) {
    val backgroundGradient = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A2420),
            Color(0xFF0E1414)
        )
    } else {
        listOf(
            Color(0xFFFAFDFD),
            Color(0xFFE8F8F4),
            Color(0xFFFAFDFD)
        )
    }
    
    val waveColors = if (isDarkMode) {
        listOf(
            MedicalTeal80.copy(alpha = 0.05f),
            HealthGreen.copy(alpha = 0.03f),
            MedicalBlue80.copy(alpha = 0.04f)
        )
    } else {
        listOf(
            MedicalTeal40.copy(alpha = 0.04f),
            HealthGreen.copy(alpha = 0.03f),
            HealthBlue.copy(alpha = 0.05f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(backgroundGradient)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Ondas sutiles de fondo
            for (i in 0..2) {
                val amplitude = height * 0.03f * (i + 1)
                val frequency = 0.008f / (i + 1)
                val phaseShift = waveOffset + (i * 90f)
                
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0f, height * 0.8f)
                
                for (x in 0..width.toInt() step 8) {
                    val y = height * 0.8f + amplitude * sin((x * frequency + phaseShift) * Math.PI / 180f).toFloat()
                    path.lineTo(x.toFloat(), y)
                }
                
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
                
                drawPath(path, waveColors[i])
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ResourcesHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Gestión de Entidades",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Seleccioná una entidad para administrar sus registros",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernResourceCard(
    type: ResourceType,
    index: Int,
    onClick: () -> Unit
) {
    val colors = getResourceCardColors(index)
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(colors)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Icono en círculo
                    Card(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = type.icon,
                                contentDescription = type.label,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Administrar ${type.label.lowercase()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // Flecha
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Cambiar por ArrowForward
                    contentDescription = "Ir",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private fun getResourceCardColors(index: Int): List<Color> {
    return when (index % 7) {
        0 -> listOf(HealthTeal, MedicalTeal80)
        1 -> listOf(HealthBlue, MedicalBlue80)
        2 -> listOf(HealthGreen, MedicalGreen80)
        3 -> listOf(MedicalTeal40, HealthTeal)
        4 -> listOf(MedicalBlue40, HealthBlue)
        5 -> listOf(MedicalGreen40, HealthGreen)
        else -> listOf(HealthOrange, Color(0xFFFFB74D))
    }
}

private fun getResourceTypes(): List<ResourceType> {
    return listOf(Paciente, Medico, Tecnico, Solicitante, Lugar, Patologia, Equipo)
}

private fun navigateToResource(navController: NavController, resourceType: ResourceType) {
    val route = when (resourceType) {
        Paciente -> NavigationRoutes.PacienteList.route
        Medico -> NavigationRoutes.MedicoList.route
        Tecnico -> NavigationRoutes.TecnicoList.route
        Solicitante -> NavigationRoutes.SolicitanteList.route
        Lugar -> NavigationRoutes.LugarList.route
        Patologia -> NavigationRoutes.PatologiaList.route
        Equipo -> NavigationRoutes.EquipoList.route
    }
    navController.navigate(route)
}
