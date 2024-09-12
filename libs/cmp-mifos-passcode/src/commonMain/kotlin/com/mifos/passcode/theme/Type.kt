package com.mifos.passcode.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Typography() = Typography().run {
    val fontFamily = LatoFonts()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}

@Composable
fun PasscodeKeyButtonStyle() = TextStyle(
    fontFamily = LatoFonts(),
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp
)

@Composable
fun skipButtonStyle() = TextStyle(
    color = blueTint,
    fontSize = 20.sp,
    fontFamily = LatoFonts()
)

@Composable
fun forgotButtonStyle() = TextStyle(
    color = blueTint,
    fontSize = 14.sp,
    fontFamily = LatoFonts()
)

@Composable
fun useTouchIdButtonStyle() = TextStyle(
    color = blueTint,
    fontSize = 14.sp,
    fontFamily = LatoFonts()
)