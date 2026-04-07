package com.example.theperfectionist

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    background = Color(0xFFA2CCFF).copy(alpha = 0.85f),
    onBackground = Color(0xFF1A1A1A),

    surface = Color(0xFFEAF4FF),
    onSurface = Color(0xFF1A1A1A),

    primary = Color(0xFF6E4BAE),
    onPrimary = Color.White,

    secondary = Color(0xFF03DAC5),
    onSecondary = Color(0xFF1A1A1A),

    outline = Color(0xFF009688)
)

private val DarkColors = darkColorScheme(
    background = Color(0xFF2B2B2B),
    onBackground = Color(0xFFF5F5F5),

    surface = Color(0xFF3A3A3A),
    onSurface = Color(0xFFF5F5F5),

    primary = Color(0xFFD1B3FF),
    onPrimary = Color(0xFF1A1A1A),

    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF1A1A1A),

    outline = Color(0xFFB0BEC5)
)

@Composable
fun PerfectionistTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}