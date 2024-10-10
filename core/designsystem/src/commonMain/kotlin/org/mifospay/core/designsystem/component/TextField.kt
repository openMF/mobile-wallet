/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
fun MfOutlinedTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = false,
    onKeyboardActions: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = {
            if (isError) {
                Text(text = errorMessage)
            }
        },
        singleLine = singleLine,
        trailingIcon = trailingIcon,
        keyboardActions = KeyboardActions {
            onKeyboardActions?.invoke()
        },
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        },
    )
}

@Composable
fun MfPasswordTextField(
    password: String,
    label: String,
    isError: Boolean,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        isError = isError,
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        supportingText = {
            errorMessage?.let { Text(text = it) }
        },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Show password",
                )
            }
        },
    )
}

@Composable
fun MifosOutlinedTextField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    icon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    error: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = icon.name,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        isError = error,
    )
}

@Preview
@Composable
fun MfOutlinedTextFieldPreview() {
    MifosTheme {
        Box(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
        ) {
            MfOutlinedTextField(
                value = "Text Field Value",
                label = "Text Field",
                onValueChange = { },
                modifier = Modifier,
                isError = true,
                errorMessage = "Error Message",
                onKeyboardActions = { },
            )
        }
    }
}

@Preview
@Composable
fun MfPasswordTextFieldPreview() {
    MifosTheme {
        val password = " "
        Box(
            modifier = Modifier.background(color = Color.White),
        ) {
            MfPasswordTextField(
                password = password,
                label = "Password",
                isError = true,
                isPasswordVisible = true,
                onTogglePasswordVisibility = { },
                onPasswordChange = { },
                modifier = Modifier.fillMaxWidth(),
                errorMessage = "Password must be at least 6 characters",
            )
        }
    }
}
