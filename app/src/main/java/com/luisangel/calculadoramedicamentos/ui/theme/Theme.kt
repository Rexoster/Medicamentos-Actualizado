package com.luisangel.calculadoramedicamentos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F4C81),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7E9FA),
    onPrimaryContainer = Color(0xFF082F55),
    secondary = Color(0xFF087F8C),
    secondaryContainer = Color(0xFFD2F2F4),
    background = Color(0xFFF4F7FB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8EEF5),
    error = Color(0xFFB42318)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF87C7FF),
    onPrimary = Color(0xFF003356),
    primaryContainer = Color(0xFF0F4C81),
    onPrimaryContainer = Color(0xFFD7E9FA),
    secondary = Color(0xFF76D5DD),
    secondaryContainer = Color(0xFF164E63),
    background = Color(0xFF070B14),
    surface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFF1E293B),
    error = Color(0xFFFFB4AB)
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
