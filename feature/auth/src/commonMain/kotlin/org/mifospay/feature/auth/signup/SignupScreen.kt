/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.auth.generated.resources.Res
import mobile_wallet.feature.auth.generated.resources.feature_auth_address_line_1
import mobile_wallet.feature.auth.generated.resources.feature_auth_address_line_2
import mobile_wallet.feature.auth.generated.resources.feature_auth_all_fields_are_mandatory
import mobile_wallet.feature.auth.generated.resources.feature_auth_complete
import mobile_wallet.feature.auth.generated.resources.feature_auth_complete_your_registration
import mobile_wallet.feature.auth.generated.resources.feature_auth_confirm_password
import mobile_wallet.feature.auth.generated.resources.feature_auth_country
import mobile_wallet.feature.auth.generated.resources.feature_auth_email
import mobile_wallet.feature.auth.generated.resources.feature_auth_first_name
import mobile_wallet.feature.auth.generated.resources.feature_auth_last_name
import mobile_wallet.feature.auth.generated.resources.feature_auth_mobile_no
import mobile_wallet.feature.auth.generated.resources.feature_auth_password
import mobile_wallet.feature.auth.generated.resources.feature_auth_pin_code
import mobile_wallet.feature.auth.generated.resources.feature_auth_state
import mobile_wallet.feature.auth.generated.resources.feature_auth_username
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopAppBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.MifosPasswordField
import org.mifospay.core.ui.PasswordStrengthIndicator
import org.mifospay.core.ui.utils.EventsEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignupScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    EventsEffect(viewModel) { event ->
        when (event) {
            is SignUpEvent.NavigateBack -> onNavigateBack.invoke()
            is SignUpEvent.NavigateToLogin -> onNavigateToLogin.invoke(event.username)
            is SignUpEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    SignUpDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(SignUpAction.ErrorDialogDismiss) }
        },
    )

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        topBar = {
            MifosTopAppBar(
                title = stringResource(Res.string.feature_auth_complete_your_registration),
                subtitle = stringResource(Res.string.feature_auth_all_fields_are_mandatory),
                scrollBehavior = scrollBehavior,
                navigationIcon = MifosIcons.Back,
                navigationIconContentDescription = "Back",
                onNavigationIconClick = remember(viewModel) {
                    { viewModel.trySendAction(SignUpAction.CloseClick) }
                },
            )
        },
    ) {
        SignupScreenContent(
            modifier = Modifier.padding(it),
            state = state,
            onAction = viewModel::trySendAction,
        )
    }
}

@Composable
private fun SignUpDialogs(
    dialogState: SignUpDialog?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is SignUpDialog.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is SignUpDialog.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
private fun SignupScreenContent(
    modifier: Modifier = Modifier,
    state: SignUpState,
    onAction: (SignUpAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MifosOutlinedTextField(
            value = state.firstNameInput,
            label = stringResource(Res.string.feature_auth_first_name),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.FirstNameInputChange(it))
            },
            isError = state.firstNameInput.isEmpty(),
        )

        MifosOutlinedTextField(
            value = state.lastNameInput,
            label = stringResource(Res.string.feature_auth_last_name),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.LastNameInputChange(it))
            },
            isError = state.lastNameInput.isEmpty(),
        )

        MifosOutlinedTextField(
            value = state.userNameInput,
            label = stringResource(Res.string.feature_auth_username),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.UserNameInputChange(it))
            },
            isError = state.userNameInput.isEmpty(),
        )

        MifosOutlinedTextField(
            value = state.emailInput,
            label = stringResource(Res.string.feature_auth_email),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.EmailInputChange(it))
            },
            isError = state.emailInput.isEmpty(),
        )

        MifosOutlinedTextField(
            value = state.mobileNumberInput,
            label = stringResource(Res.string.feature_auth_mobile_no),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.MobileNumberInputChange(it))
            },
            isError = state.mobileNumberInput.isEmpty(),
        )

        var showPassword by rememberSaveable { mutableStateOf(false) }

        MifosPasswordField(
            value = state.passwordInput,
            label = stringResource(Res.string.feature_auth_password),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.PasswordInputChange(it))
            },
            showPassword = showPassword,
            showPasswordChange = { showPassword = !showPassword },
        )
        PasswordStrengthIndicator(
            modifier = Modifier.padding(horizontal = 16.dp),
            state = state.passwordStrengthState,
            currentCharacterCount = state.passwordInput.length,
        )
        MifosPasswordField(
            value = state.confirmPasswordInput,
            label = stringResource(Res.string.feature_auth_confirm_password),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.ConfirmPasswordInputChange(it))
            },
            showPassword = showPassword,
            showPasswordChange = { showPassword = !showPassword },
        )

        MifosOutlinedTextField(
            value = state.addressLine1Input,
            label = stringResource(Res.string.feature_auth_address_line_1),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.AddressLine1InputChange(it))
            },
            isError = state.addressLine1Input.isEmpty(),
        )

        MifosOutlinedTextField(
            value = state.addressLine2Input,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.feature_auth_address_line_2),
            onValueChange = {
                onAction(SignUpAction.AddressLine2InputChange(it))
            },
            isError = state.addressLine2Input.isEmpty(),
        )

        MifosOutlinedTextField(
            value = state.pinCodeInput,
            label = stringResource(Res.string.feature_auth_pin_code),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                onAction(SignUpAction.PinCodeInputChange(it))
            },
            isError = state.pinCodeInput.isEmpty(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MifosOutlinedTextField(
                value = state.countryInput,
                label = stringResource(Res.string.feature_auth_country),
                onValueChange = {
                    onAction(SignUpAction.CountryInputChange(it))
                },
                modifier = Modifier.weight(1.5f),
                isError = state.countryInput.isEmpty(),
            )

            MifosOutlinedTextField(
                value = state.stateInput,
                label = stringResource(Res.string.feature_auth_state),
                onValueChange = {
                    onAction(SignUpAction.StateInputChange(it))
                },
                modifier = Modifier.weight(1.5f),
                isError = state.stateInput.isEmpty(),
            )
        }

        MifosButton(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            enabled = true,
            onClick = {
                onAction(SignUpAction.SubmitClick)
            },
            contentPadding = PaddingValues(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_complete),
            )
        }
    }
}
