/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.chooseAuthOption

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile_wallet.feature.auth.generated.resources.Res
import mobile_wallet.feature.auth.generated.resources.feature_auth_continue
import mobile_wallet.feature.auth.generated.resources.feature_auth_device_lock_subtitle
import mobile_wallet.feature.auth.generated.resources.feature_auth_enable_app_lock
import mobile_wallet.feature.auth.generated.resources.feature_auth_error
import mobile_wallet.feature.auth.generated.resources.feature_auth_mifos_passcode_subtitle
import mobile_wallet.feature.auth.generated.resources.feature_auth_no
import mobile_wallet.feature.auth.generated.resources.feature_auth_no_authentication_options_set
import mobile_wallet.feature.auth.generated.resources.feature_auth_ok
import mobile_wallet.feature.auth.generated.resources.feature_auth_setup_authentication_options
import mobile_wallet.feature.auth.generated.resources.feature_auth_use_mifos_passcode
import mobile_wallet.feature.auth.generated.resources.feature_auth_use_your_device_lock
import mobile_wallet.feature.auth.generated.resources.feature_auth_yes
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifospay.core.data.util.AppLockOption
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.feature.auth.chooseAuthOption.components.AuthOptionCard
import template.core.base.ui.EventsEffect

@Composable
fun ChooseAuthOptionScreen(
    onBiometricsRegistrationSuccess: () -> Unit,
    onNavigateToPasscode: () -> Unit,
    viewModel: ChooseAuthOptionScreenViewmodel = koinViewModel(),
) {
    val platformAuthenticationProvider = platformAuthenticationProvider.current

    val state by viewModel.stateFlow.collectAsState()

    EventsEffect(viewModel) { event ->
        when (event) {
            ChooseAuthOptionScreenEvents.BiometricRegistrationSuccess -> onBiometricsRegistrationSuccess()
            ChooseAuthOptionScreenEvents.OnNavigateToPasscode -> onNavigateToPasscode()
        }
    }

    ChooseAuthScreenDialogBox(
        screenState = state.screenState,
        platformAuthenticationProvider = platformAuthenticationProvider,
        onAction = viewModel::trySendAction,
    )

    ChooseAuthOptionContent(
        state = state,
        platformAuthenticationProvider = platformAuthenticationProvider,
        onAction = viewModel::trySendAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAuthOptionContent(
    state: ChooseAuthOptionScreenUiState,
    platformAuthenticationProvider: PlatformAuthenticationProvider,
    onAction: (ChooseAuthOptionScreenAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.feature_auth_enable_app_lock), fontSize = 24.sp) },
            )
        },
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
                .fillMaxSize()
                .padding(it),
        ) {
            Column {
                AuthOptionCard(
                    selected = state.selectedAuthOption == AppLockOption.DeviceLock,
                    title = stringResource(Res.string.feature_auth_use_your_device_lock),
                    subtitle = stringResource(Res.string.feature_auth_device_lock_subtitle),
                    icon = Icons.Default.Dialpad,
                    onSelect = {
                        onAction(ChooseAuthOptionScreenAction.OnSelectDeviceLock)
                    },
                )

                Spacer(Modifier.height(10.dp))

                AuthOptionCard(
                    selected = state.selectedAuthOption == AppLockOption.MifosPasscode,
                    title = stringResource(Res.string.feature_auth_use_mifos_passcode),
                    subtitle = stringResource(Res.string.feature_auth_mifos_passcode_subtitle),
                    icon = Icons.Default.People,
                    onSelect = {
                        onAction(ChooseAuthOptionScreenAction.OnSelectPasscode)
                    },
                )
            }

            Button(
                onClick = {
                    navigationHelper(
                        state.selectedAuthOption,
                        whenDeviceLockSelected = {
                            onAction(
                                ChooseAuthOptionScreenAction.RegisterUserBiometrics(platformAuthenticationProvider),
                            )
                        },
                        whenPasscodeSelected = {
                            onAction(ChooseAuthOptionScreenAction.NavigateToPasscode)
                        },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                enabled = state.selectedAuthOption != AppLockOption.None,
            ) {
                Text(stringResource(Res.string.feature_auth_continue))
            }
        }
    }
}

@Composable
fun ChooseAuthScreenDialogBox(
    screenState: ChooseAuthOptionScreenUiState.ScreenState?,
    platformAuthenticationProvider: PlatformAuthenticationProvider,
    onAction: (ChooseAuthOptionScreenAction) -> Unit,
) {
    when (screenState) {
        ChooseAuthOptionScreenUiState.ScreenState.AuthenticatorNotSetup -> {
            MifosDialogBox(
                title = stringResource(Res.string.feature_auth_no_authentication_options_set),
                message = stringResource(Res.string.feature_auth_setup_authentication_options),
                showDialogState = true,
                confirmButtonText = stringResource(Res.string.feature_auth_yes),
                dismissButtonText = stringResource(Res.string.feature_auth_no),
                onConfirm = {
                    onAction(ChooseAuthOptionScreenAction.SetupPlatformAuthenticator(platformAuthenticationProvider))
                },
                onDismiss = {
                    onAction(ChooseAuthOptionScreenAction.DismissDialogBox)
                },
            )
        }
        is ChooseAuthOptionScreenUiState.ScreenState.Error -> {
            MifosDialogBox(
                title = stringResource(Res.string.feature_auth_error),
                showDialogState = true,
                confirmButtonText = stringResource(Res.string.feature_auth_ok),
                dismissButtonText = null,
                message = screenState.message,
                onConfirm = {
                    onAction(ChooseAuthOptionScreenAction.DismissDialogBox)
                },
                onDismiss = {
                    onAction(ChooseAuthOptionScreenAction.DismissDialogBox)
                },
            )
        }
        null -> {}
    }
}

private fun navigationHelper(
    option: AppLockOption,
    whenDeviceLockSelected: () -> Unit,
    whenPasscodeSelected: () -> Unit,
) {
    if (option == AppLockOption.DeviceLock) whenDeviceLockSelected()
    if (option == AppLockOption.MifosPasscode) whenPasscodeSelected()
}
