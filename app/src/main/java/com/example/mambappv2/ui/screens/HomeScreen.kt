// HomeScreen.kt
package com.example.mambappv2.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun HomeScreen(
    onNavigateToNew: () -> Unit,
    onNavigateToList: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()

    // Transición animada suave que va y vuelve
    val transition = rememberInfiniteTransition(label = "gradient_animation")
    val offset by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val gradientColors = if (isDarkMode) {
        listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF0D47A1)) // simétrico
    } else {
        listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA), Color(0xFFB2EBF2)) // simétrico
    }

    val gradientBrush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(offset, 0f),
        end = Offset(offset + 1000f, 1000f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MambApp V2",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else Color.Black
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionButton(text = "Nuevo Monitoreo", isDark = isDarkMode, onClick = onNavigateToNew)
                ActionButton(text = "Ver Registros", isDark = isDarkMode, onClick = onNavigateToList)
            }
        }
    }
}

@Composable
fun ActionButton(text: String, isDark: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDark) Color(0xFF1E88E5) else Color(0xFF5ECDEE),
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
