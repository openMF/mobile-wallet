package org.mifos.mobilewallet.mifospay.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mifos.mobilewallet.model.State
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme


@Composable
fun MfOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = false,
    onValueChange: (String) -> Unit,
    onKeyboardActions: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
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
            focusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp, color = Color.Black)
        }
    )
}

@Composable
fun MfPasswordTextField(
    modifier: Modifier = Modifier,
    password: String,
    label: String,
    isError: Boolean,
    errorMessage: String? = null,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onPasswordChange: (String) -> Unit,
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
                    contentDescription = "Show password"
                )
            }
        }
    )
}


@Composable
fun MifosOutlinedTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    maxLines: Int = 1,
    modifier: Modifier,
    singleLine: Boolean = true,
    icon: Int? = null,
    label: Int,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    error: Boolean = false,
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
                    colorFilter = if (isSystemInDarkTheme()) {
                        ColorFilter.tint(Color.White)
                    } else ColorFilter.tint(
                        Color.Black
                    )
                )
            }
        } else null,
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp, color = Color.Black)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        isError = error
    )
}

@Preview
@Composable
fun MfOutlinedTextFieldPreview() {
    MifosTheme {
        Box(
            modifier = Modifier.background(color = Color.White)
        ) {
            MfOutlinedTextField(
                modifier = Modifier,
                value = "Text Field Value",
                label = "Text Field",
                isError = true,
                errorMessage = "Error Message",
                onValueChange = { },
                onKeyboardActions = { }
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
            modifier = Modifier.background(color = Color.White)
        ) {
            MfPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                password = password,
                label = "Password",
                isError = password.isEmpty() || password.length < 6,
                errorMessage = if (password.isEmpty()) {
                    "Password cannot be empty"
                } else if (password.length < 6) {
                    "Password must be at least 6 characters"
                } else null,
                onPasswordChange = { },
                isPasswordVisible = true,
                onTogglePasswordVisibility = { }
            )
        }
    }
}

