package com.mifos.passcode.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue

private val DarkColorPalette = darkColorScheme(
    primary = Color.Cyan,
    onPrimary = Color.Cyan,
    secondary = Color.Black.copy(alpha = 0.2f),
    background = Color.Black
)
private val LightColorPalette = lightColorScheme(
    primary = Blue,
    onPrimary = Blue,
    secondary = Color.Blue.copy(alpha = 0.4f),
    background = Color.White
)

@Composable
fun MifosPasscodeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}