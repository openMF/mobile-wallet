/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mobile_wallet.core.ui.generated.resources.Res
import mobile_wallet.core.ui.generated.resources.core_ui_hint_icon_description
import mobile_wallet.core.ui.generated.resources.core_ui_view_password_rules
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.component.MifosCustomTextField
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.utils.tabNavigation

@Composable
fun MifosPasswordField(
    label: String,
    value: String,
    showPassword: Boolean,
    showPasswordChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    hintMessage: String? = null,
    onHintClick: (() -> Unit)? = null,
    showPasswordTestTag: String? = null,
    autoFocus: Boolean = false,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Password,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focusRequester = remember { FocusRequester() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    MifosCustomTextField(
        modifier = modifier
            .tabNavigation()
            .focusRequester(focusRequester),
        label = label,
        value = value,
        onValueChange = onValueChange,
        visualTransformation = when {
            !showPassword -> PasswordVisualTransformation()
            readOnly -> nonLetterColorVisualTransformation()
            else -> VisualTransformation.None
        },
        singleLine = singleLine,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        isError = isError && isFocused,
        keyboardActions = keyboardActions,
        supportingText = {
            // Supporting text section shows either an error message or a help hint depending on context
            if (hintMessage != null && isFocused) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (onHintClick != null) {
                        // If a hint click handler is available:
                        // - Show the error message when `isError` is true
                        // - Otherwise, show a help hint for password rules
                        // - Always display the help icon button to show password rules
                        Text(
                            text = if (isError) hintMessage else stringResource(Res.string.core_ui_view_password_rules),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        FilledIconButton(
                            onClick = onHintClick,
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                imageVector = MifosIcons.QuestionMark,
                                contentDescription = stringResource(Res.string.core_ui_hint_icon_description),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    } else {
                        // If there's no hint icon to show, just display the error text if needed
                        if (isError) {
                            Text(
                                text = hintMessage,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        },
        trailingIcon = {
            IconButton(
                onClick = { showPasswordChange.invoke(!showPassword) },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                val imageVector = if (showPassword) {
                    MifosIcons.OutlinedVisibilityOff
                } else {
                    MifosIcons.OutlinedVisibility
                }

                Icon(
                    modifier = Modifier.semantics { showPasswordTestTag?.let { testTag = it } },
                    imageVector = imageVector,
                    contentDescription = "togglePassword",
                )
            }
        },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        interactionSource = interactionSource,
    )
    if (autoFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}

@Composable
fun MifosPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    hint: String? = null,
    initialShowPassword: Boolean = false,
    showPasswordTestTag: String? = null,
    autoFocus: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Password,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var showPassword by rememberSaveable { mutableStateOf(initialShowPassword) }
    MifosPasswordField(
        modifier = modifier,
        label = label,
        value = value,
        showPassword = showPassword,
        showPasswordChange = { showPassword = !showPassword },
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = singleLine,
        hintMessage = hint,
        showPasswordTestTag = showPasswordTestTag,
        autoFocus = autoFocus,
        keyboardType = keyboardType,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
    )
}

@Preview
@Composable
private fun MifosPasswordField_preview_withInput_hidePassword() {
    MifosPasswordField(
        label = "Label",
        value = "Password",
        onValueChange = {},
        initialShowPassword = false,
        hint = "Hint",
    )
}

@Preview
@Composable
private fun MifosPasswordField_preview_withInput_showPassword() {
    MifosPasswordField(
        label = "Label",
        value = "Password",
        onValueChange = {},
        initialShowPassword = true,
        hint = "Hint",
    )
}

@Preview
@Composable
private fun MifosPasswordField_preview_withoutInput_hidePassword() {
    MifosPasswordField(
        label = "Label",
        value = "",
        onValueChange = {},
        initialShowPassword = false,
        hint = "Hint",
    )
}

@Preview
@Composable
private fun MifosPasswordField_preview_withoutInput_showPassword() {
    MifosPasswordField(
        label = "Label",
        value = "",
        onValueChange = {},
        initialShowPassword = true,
        hint = "Hint",
    )
}
