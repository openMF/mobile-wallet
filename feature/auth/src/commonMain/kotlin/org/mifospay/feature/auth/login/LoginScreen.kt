/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.auth.generated.resources.Res
import mobile_wallet.feature.auth.generated.resources.feature_auth_login
import mobile_wallet.feature.auth.generated.resources.feature_auth_password
import mobile_wallet.feature.auth.generated.resources.feature_auth_sign_up
import mobile_wallet.feature.auth.generated.resources.feature_auth_username
import mobile_wallet.feature.auth.generated.resources.feature_auth_welcome_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.grey
import org.mifospay.core.designsystem.theme.styleNormal18sp
import org.mifospay.core.ui.MifosPasswordField
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun LoginScreen(
    onNavigateBack: () -> Unit,
    navigateToPasscodeScreen: () -> Unit,
    navigateToSignupScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is LoginEvent.NavigateBack -> onNavigateBack.invoke()
            is LoginEvent.NavigateToSignup -> navigateToSignupScreen.invoke()
            is LoginEvent.NavigateToPasscodeScreen -> navigateToPasscodeScreen.invoke()
            is LoginEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    LoginDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(LoginAction.ErrorDialogDismiss) }
        },
    )

    LoginScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (LoginAction) -> Unit,
) {
    MifosScaffold(
        snackbarHostState = snackbarHostState,
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LoginScreenContent(
            state = state,
            onAction = onAction,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
private fun LoginDialogs(
    dialogState: LoginState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is LoginState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is LoginState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    modifier: Modifier = Modifier,
    onAction: (LoginAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(Res.string.feature_auth_login),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier
                .padding(top = 24.dp),
            text = stringResource(Res.string.feature_auth_welcome_back),
            style = styleNormal18sp.copy(color = grey),
        )
        Spacer(modifier = Modifier.padding(top = 32.dp))
        MifosOutlinedTextField(
            label = stringResource(Res.string.feature_auth_username),
            value = state.username,
            onValueChange = {
                onAction(LoginAction.UsernameChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        MifosPasswordField(
            label = stringResource(Res.string.feature_auth_password),
            value = state.password,
            onValueChange = {
                onAction(LoginAction.PasswordChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            showPassword = state.isPasswordVisible,
            showPasswordChange = {
                onAction(LoginAction.TogglePasswordVisibility)
            },
        )
        val isLoginButtonEnabled = state.username.isNotEmpty() && state.password.isNotEmpty()
        MifosButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = isLoginButtonEnabled,
            onClick = {
                onAction(LoginAction.LoginClicked)
            },
            contentPadding = PaddingValues(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_login).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        SignupButton(
            navigateToSignupScreen = {
                onAction(LoginAction.SignupClicked)
            },
        )
    }
}

@Composable
private fun SignupButton(
    navigateToSignupScreen: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Don’t have an account yet? ",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                navigateToSignupScreen()
            },
            text = stringResource(Res.string.feature_auth_sign_up),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge.copy(
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

@Preview
@Composable
private fun LoanScreenPreview() {
    MifosTheme {
        LoginScreen(
            state = LoginState(dialogState = null),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {},
        )
    }
}
