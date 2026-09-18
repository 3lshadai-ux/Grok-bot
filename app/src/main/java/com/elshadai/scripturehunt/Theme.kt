package com.elshadai.scripturehunt

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ScriptureHuntTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFC9A227),
            secondary = Color(0xFF1A4D4A),
            background = Color(0xFF1A4D4A),
            surface = Color(0xFFFFF8E7),
            onPrimary = Color(0xFF3D2914),
            onSurface = Color(0xFF3D2914),
        ),
        content = content,
    )
}
