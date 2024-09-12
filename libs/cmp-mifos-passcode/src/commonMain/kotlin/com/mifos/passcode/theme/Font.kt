package com.mifos.passcode.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.mifos.passcode.resources.Res
import com.mifos.passcode.resources.lato_black
import com.mifos.passcode.resources.lato_bold
import com.mifos.passcode.resources.lato_regular
import org.jetbrains.compose.resources.Font

@Composable
fun LatoFonts() = FontFamily(
    Font(
        resource = Res.font.lato_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        resource = Res.font.lato_bold,
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    ),
    Font(
        resource = Res.font.lato_black,
        weight = FontWeight.Black,
        style = FontStyle.Normal
    )
)