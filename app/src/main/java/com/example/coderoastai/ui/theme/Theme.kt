package com.example.coderoastai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberpunkColorScheme = darkColorScheme(
    primary = NeonRed,
    onPrimary = TextPrimary,
    primaryContainer = NeonRedDark,
    onPrimaryContainer = TextPrimary,

    secondary = NeonCyan,
    onSecondary = DeepBlack,
    secondaryContainer = NeonCyanDark,
    onSecondaryContainer = TextPrimary,

    tertiary = NeonPurple,
    onTertiary = TextPrimary,

    background = DeepBlack,
    onBackground = TextPrimary,

    surface = DarkGray,
    onSurface = TextPrimary,
    surfaceVariant = MidGray,
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = TextPrimary,

    outline = GlassWhite20,
    outlineVariant = GlassWhite10
)

@Composable
fun CodeRoastaiTheme(
    darkTheme: Boolean = true, // Always dark theme for cyberpunk aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = CyberpunkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}