package com.example.mambappv2.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mambappv2.ui.theme.*
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToNew: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToResources: () -> Unit
) {
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF0E1414)
    
    // Animaciones fluidas
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )
    
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_animation"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo con gradiente animado
        AnimatedBackground(
            isDarkMode = isDarkMode,
            waveOffset = waveOffset,
            gradientOffset = gradientOffset
        )
        
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con logo y título
            ModernHeader()
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Cards de navegación principales
            NavigationCardsSection(
                onNavigateToNew = onNavigateToNew,
                onNavigateToList = onNavigateToList,
                onNavigateToResources = onNavigateToResources,
                isDarkMode = isDarkMode
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer con información profesional
            ProfessionalFooter(isDarkMode = isDarkMode)
        }
    }
}

@Composable
private fun AnimatedBackground(
    isDarkMode: Boolean,
    waveOffset: Float,
    gradientOffset: Float
) {
    val gradientColors = if (isDarkMode) {
        listOf(
            Color(0xFF0E1414),
            Color(0xFF1A2B2B),
            Color(0xFF0E1414)
        )
    } else {
        listOf(
            Color(0xFFFAFDFD),
            Color(0xFFE8F4F4),
            Color(0xFFFAFDFD)
        )
    }
    
    val accentGradient = if (isDarkMode) {
        listOf(
            MedicalTeal80.copy(alpha = 0.1f),
            MedicalBlue80.copy(alpha = 0.05f),
            MedicalGreen80.copy(alpha = 0.1f)
        )
    } else {
        listOf(
            MedicalTeal40.copy(alpha = 0.08f),
            HealthBlue.copy(alpha = 0.05f),
            HealthGreen.copy(alpha = 0.08f)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Gradiente base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors,
                        startY = gradientOffset,
                        endY = 1000f + gradientOffset
                    )
                )
        )
        
        // Ondas animadas
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            
            for (i in 0..2) {
                val waveColor = accentGradient[i]
                val amplitude = height * 0.05f * (i + 1)
                val frequency = 0.01f / (i + 1)
                val phaseShift = waveOffset + (i * 120f)
                
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0f, height * 0.7f)
                
                for (x in 0..width.toInt() step 5) {
                    val y = height * 0.7f + amplitude * sin((x * frequency + phaseShift) * Math.PI / 180f).toFloat()
                    path.lineTo(x.toFloat(), y)
                }
                
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
                
                drawPath(path, waveColor)
            }
        }
    }
}

@Composable
private fun ModernHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo/Icono médico moderno
        Card(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = "MambApp Logo",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Título principal
        Text(
            text = "MambApp V2",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtítulo profesional
        Text(
            text = "Sistema de Monitoreo Neurológico",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NavigationCardsSection(
    onNavigateToNew: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToResources: () -> Unit,
    isDarkMode: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card principal - Nuevo Monitoreo
        PrimaryActionCard(
            title = "Nuevo Monitoreo",
            subtitle = "Registrar procedimiento",
            icon = Icons.Default.Add,
            onClick = onNavigateToNew,
            gradient = listOf(HealthTeal, MedicalTeal80)
        )
        
        // Cards secundarias
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecondaryActionCard(
                modifier = Modifier.weight(1f),
                title = "Historial",
                subtitle = "Ver registros",
                icon = Icons.Default.History,
                onClick = onNavigateToList
            )
            
            SecondaryActionCard(
                modifier = Modifier.weight(1f),
                title = "Recursos",
                subtitle = "Gestionar datos",
                icon = Icons.Default.Settings,
                onClick = onNavigateToResources
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrimaryActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    gradient: List<Color>
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradient)
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Card(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecondaryActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfessionalFooter(isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = "Verificado",
                modifier = Modifier.size(20.dp),
                tint = HealthGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sistema certificado para uso exclusivo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
