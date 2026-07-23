package com.example.schoolclockgame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClockColorScheme = lightColorScheme(
    primary = Color(0xFF246BFD),
    secondary = Color(0xFFFFB020),
    tertiary = Color(0xFF18A058),
    background = Color(0xFFF7F8FB),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color(0xFF231A00),
    onTertiary = Color.White,
    onBackground = Color(0xFF1F2430),
    onSurface = Color(0xFF1F2430),
)

@Composable
fun SchoolClockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ClockColorScheme,
        content = content,
    )
}
