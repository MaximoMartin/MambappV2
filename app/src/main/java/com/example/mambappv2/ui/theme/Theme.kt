package com.example.mambappv2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MedicalTeal40,
    secondary = HealthBlue,
    tertiary = HealthGreen,
    background = Color(0xFFFAFDFD),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F8F8),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = MedicalDark40,
    onSurface = MedicalDark40,
    onSurfaceVariant = Color(0xFF6B7B7B),
    primaryContainer = MedicalLightBlue80,
    onPrimaryContainer = MedicalTeal40,
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = MedicalBlue40,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTeal80,
    secondary = MedicalBlue80,
    tertiary = MedicalGreen80,
    background = Color(0xFF0E1414),
    surface = Color(0xFF1A1F1F),
    surfaceVariant = Color(0xFF232B2B),
    onPrimary = MedicalDark40,
    onSecondary = MedicalDark40,
    onTertiary = MedicalDark40,
    onBackground = Color(0xFFE8F4F4),
    onSurface = Color(0xFFE8F4F4),
    onSurfaceVariant = Color(0xFFB8C8C8),
    primaryContainer = MedicalTeal40,
    onPrimaryContainer = MedicalTeal80,
    secondaryContainer = MedicalBlue40,
    onSecondaryContainer = MedicalBlue80,
    error = Color(0xFFFF8A80),
    onError = MedicalDark40,
    errorContainer = Color(0xFF5D1A1A),
    onErrorContainer = Color(0xFFFF8A80)
)

@Composable
fun MambAppV2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
