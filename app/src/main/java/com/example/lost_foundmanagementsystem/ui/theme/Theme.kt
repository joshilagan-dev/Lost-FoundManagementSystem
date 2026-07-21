package com.example.lost_foundmanagementsystem.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121A1A),
    surface = Color(0xFF1A2424)
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    secondary = Navy,
    onSecondary = Color.White,
    tertiary = AccentCoral,
    background = Sand,
    surface = Color.White,
    onBackground = Navy,
    onSurface = Navy
)

@Composable
fun LostFoundManagementSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Prefer fixed campus palette over dynamic purple system colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
