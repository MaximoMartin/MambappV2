package com.example.mambappv2.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onNavigateToNew: () -> Unit,
    onNavigateToList: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val offsetAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "gradient_shift"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF3BF6E0), Color(0xFF03A9F4), Color(0xFF673AB7)),
                    start = Offset(0f, offsetAnim),
                    end = Offset(offsetAnim, 0f)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MambApp V2",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.White.copy(alpha = 0.6f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
            Spacer(modifier = Modifier.height(64.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadicalButton(text = "📝 Nuevo Monitoreo") { onNavigateToNew() }
                RadicalButton(text = "📚 Ver Registros") { onNavigateToList() }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }

        // Background blobs
        Canvas(modifier = Modifier.fillMaxSize().blur(100.dp)) {
            drawIntoCanvas {
                drawCircle(Color(0xFF000DFF), radius = 300f, center = Offset(200f, 300f))
                drawCircle(Color(0xFFF44336), radius = 400f, center = Offset(size.width - 100f, size.height - 200f))
            }
        }
    }
}

@Composable
fun RadicalButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .background(Color.Black, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
