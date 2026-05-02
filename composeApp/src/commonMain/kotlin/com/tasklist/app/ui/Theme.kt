package com.tasklist.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================================
// Primary = main color (buttons, highlights, checkboxes)
// Secondary = accent color (text inside button, etc...)
// Tertiary = optional third accent
// ============================================================

private val LightColors = lightColorScheme(
    primary = Color(0xFF006874),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF97F0FF),
    onPrimaryContainer = Color(0xFF001F24),
    secondary = Color(0xFF4A6267),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE7EC),
    onSecondaryContainer = Color(0xFF051F23),
    tertiary = Color(0xFF525E7D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDAE2FF),
    onTertiaryContainer = Color(0xFF0E1B37),
    background = Color(0xFFF5FAFB),
    onBackground = Color(0xFF171C1E),
    surface = Color(0xFFF5FAFB),
    onSurface = Color(0xFF171C1E),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)


private val DarkColors = darkColorScheme(
    primary = Color(0xFF4FD8EB),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = Color(0xFFB1CBD0),
    onSecondary = Color(0xFF1C3438),
    secondaryContainer = Color(0xFF324B4F),
    onSecondaryContainer = Color(0xFFCCE7EC),
    tertiary = Color(0xFFBAC6EA),
    onTertiary = Color(0xFF24304D),
    tertiaryContainer = Color(0xFF3A4664),
    onTertiaryContainer = Color(0xFFDAE2FF),
    background = Color(0xFF0E1415),
    onBackground = Color(0xFFDDE3E5),
    surface = Color(0xFF11191A),
    onSurface = Color(0xFFDDE3E5),
    error = Color(0xFFFF5244),
    onError = Color(0xFF690005),
)

@Composable
fun TasklistTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}