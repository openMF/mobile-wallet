package org.mifospay.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

@Composable
fun MifosOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1,
    modifier: Modifier,
    singleLine: Boolean = true,
    icon: Int? = null,
    label: Int,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    error: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = label)) },
        modifier = modifier,
        leadingIcon = if (icon != null) {
            {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        } else null,
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        isError = error
    )
}