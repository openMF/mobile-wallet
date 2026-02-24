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
import androidx.lifecycle.viewmodel.compose.viewModel
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
                title = { Text("Enable app lock", fontSize = 24.sp) },
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
                    title = "Use your device lock",
                    subtitle = "Use your existing PIN, password, pattern, face ID, or fingerprint",
                    icon = Icons.Default.Dialpad,
                    onSelect = {
                        onAction(ChooseAuthOptionScreenAction.OnSelectDeviceLock)
                    },
                )

                Spacer(Modifier.height(10.dp))

                AuthOptionCard(
                    selected = state.selectedAuthOption == AppLockOption.MifosPasscode,
                    title = "Use 4-digit Mifos Passcode",
                    subtitle = "Use your Mifos Passcode",
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
                Text("Continue")
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
                title = "No authentication options set",
                message = "Setup authentication options",
                showDialogState = true,
                confirmButtonText = "Yes",
                dismissButtonText = "No",
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
                title = "Error",
                showDialogState = true,
                confirmButtonText = "",
                dismissButtonText = "Ok",
                message = screenState.message,
                onConfirm = {
                    onAction(ChooseAuthOptionScreenAction.SetupPlatformAuthenticator(platformAuthenticationProvider))
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
