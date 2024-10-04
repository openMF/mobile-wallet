/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.editpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MfPasswordTextField
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.MifosBlue
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.NewUi.gradientOne


@Composable
internal fun EditPasswordScreen(
    onBackPress: () -> Unit,
    onCancelChanges: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditPasswordViewModel = koinViewModel(),
) {
    val editPasswordUiState by viewModel.editPasswordUiState.collectAsStateWithLifecycle()
    EditPasswordScreen(
        modifier = modifier,
        editPasswordUiState = editPasswordUiState,
        onCancelChanges = onCancelChanges,
        onBackPress = onBackPress,
        onSave = { currentPass, newPass, confirmPass ->
            viewModel.updatePassword(
                currentPassword = currentPass,
                newPassword = newPass,
                newPasswordRepeat = confirmPass,
            )
        },
    )
}

@Composable
private fun EditPasswordScreen(
    editPasswordUiState: EditPasswordUiState,
    onCancelChanges: () -> Unit,
    onBackPress: () -> Unit,
    onSave: (currentPass: String, newPass: String, confirmPass: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isNewPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmNewPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val currentSnackbarHostState by rememberUpdatedState(snackbarHostState)

    LaunchedEffect(editPasswordUiState) {
        when (editPasswordUiState) {
            is EditPasswordUiState.Error -> {
                val errorMessage = editPasswordUiState.message
                coroutineScope.launch {
                    currentSnackbarHostState.showSnackbar(errorMessage)
                }
            }

            EditPasswordUiState.Loading -> {}
            EditPasswordUiState.Success -> {
                coroutineScope.launch {
                    currentSnackbarHostState.showSnackbar(
                        context.getString(R.string.feature_editpassword_password_changed_successfully),
                    )
                }
            }
        }
    }

    MifosScaffold(
        modifier = modifier,
        topBarTitle = R.string.feature_editpassword_change_password,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        backPress = onBackPress,
        scaffoldContent = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement =  Arrangement.Center
            ) {
                MfPasswordTextField(
                    password = currentPassword,
                    label = stringResource(R.string.feature_editpassword_current_password),
                    isError = false,
                    isPasswordVisible = isConfirmPasswordVisible,
                    onTogglePasswordVisibility = {
                        isConfirmPasswordVisible = !isConfirmPasswordVisible
                    },
                    onPasswordChange = { currentPassword = it },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp),

                    )
                MfPasswordTextField(
                    password = newPassword,

                    label = stringResource(id = R.string.feature_editpassword_new_password),
                    isError = newPassword.isNotEmpty() && newPassword.length < 6,
                    isPasswordVisible = isNewPasswordVisible,
                    onTogglePasswordVisibility = { isNewPasswordVisible = !isNewPasswordVisible },
                    onPasswordChange = { newPassword = it },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp),
                    errorMessage = if (newPassword.isNotEmpty() && newPassword.length < 6) {
                        stringResource(
                            id = R.string.feature_editpassword_password_length_error,
                        )
                    } else {
                        null
                    },
                )
                MfPasswordTextField(
                    password = confirmNewPassword,
                    label = stringResource(id = R.string.feature_editpassword_confirm_new_password),
                    isError = newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty(),
                    isPasswordVisible = isConfirmNewPasswordVisible,
                    onTogglePasswordVisibility = {
                        isConfirmNewPasswordVisible = !isConfirmNewPasswordVisible
                    },
                    onPasswordChange = { confirmNewPassword = it },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp),

                    errorMessage = if (newPassword !=
                        confirmNewPassword && confirmNewPassword.isNotEmpty()
                    ) {
                        stringResource(
                            id = R.string.feature_editpassword_password_mismatch_error,
                        )
                    } else {
                        null
                    },
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Alinha os botões ao centro
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    MifosButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(54.dp),
                        color = MifosBlue,  // Cor do botão de "Salvar"
                        onClick = {
                            onSave.invoke(currentPassword, newPassword, confirmNewPassword)
                        },
                        contentPadding = PaddingValues(16.dp),
                        content = { Text(text = stringResource(id = R.string.feature_editpassword_save)) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))



                    MifosButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(54.dp),
                        color = gradientOne,

                        onClick = {
                            onCancelChanges.invoke()
                        },
                        contentPadding = PaddingValues(16.dp),
                        content = {
                            Text(
                                text = stringResource(id = R.string.feature_editpassword_cancel),
                                color = MifosBlue,
                            )
                        },
                    )
                }
            }
        },
    )
}

class EditPasswordUiStateProvider : PreviewParameterProvider<EditPasswordUiState> {
    override val values: Sequence<EditPasswordUiState>
        get() = sequenceOf(
            EditPasswordUiState.Loading,
            EditPasswordUiState.Success,
            EditPasswordUiState.Error("Some Error Occurred"),
        )
}

@Preview
@Composable
private fun EditPasswordScreenPreview(
    @PreviewParameter(EditPasswordUiStateProvider::class) editPasswordUiState: EditPasswordUiState,
) {
    MifosTheme {
        EditPasswordScreen(editPasswordUiState = editPasswordUiState, {}, {}, { _, _, _ -> })
    }
}
