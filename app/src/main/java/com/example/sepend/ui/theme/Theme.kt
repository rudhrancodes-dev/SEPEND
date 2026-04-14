package com.example.sepend.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CosmicColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CosmicBackground,
    primaryContainer = NeonPurple.copy(alpha = 0.2f),
    secondary = NeonPurple,
    onSecondary = TextPrimary,
    tertiary = LightPeriwinkle,
    background = CosmicBackground,
    onBackground = TextPrimary,
    surface = CosmicSurface,
    onSurface = TextPrimary,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun SEPENDTheme(
    // We force dark theme, ignoring system setting for "Midnight Luxury"
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = CosmicColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}