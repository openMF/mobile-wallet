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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.mifos.authenticator.biometrics.libraryLocalPlatformAuthenticationProvider
import org.mifos.authenticator.passcode.components.DialogButton
import org.mifos.authenticator.passcode.theme.blueTint
import org.mifospay.feature.auth.chooseAuthOption.components.AuthOptionCard
import template.core.base.ui.EventsEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAuthOptionScreen(
    viewModel: ChooseAuthOptionScreenViewmodel,
    onBiometricsRegistrationSuccess: () -> Unit,
    onChoosePasscode: () -> Unit,
) {
    val platformAuthenticationProvider = libraryLocalPlatformAuthenticationProvider.current

    val state by viewModel.stateFlow.collectAsState()

    EventsEffect(viewModel) { event ->
        when (event) {
            ChooseAuthOptionScreenEvents.BiometricRegistrationSuccess -> onBiometricsRegistrationSuccess()
            ChooseAuthOptionScreenEvents.OnChoosePasscode -> onChoosePasscode()
        }
    }

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
                        viewModel.trySendAction(ChooseAuthOptionScreenActions.OnSelectDeviceLock)
                    },
                )

                Spacer(Modifier.height(10.dp))

                AuthOptionCard(
                    selected = state.selectedAuthOption == AppLockOption.MifosPasscode,
                    title = "Use 6-digit Mifos Passcode",
                    subtitle = "Use your Mifos Passcode",
                    icon = Icons.Default.People,
                    onSelect = {
                        viewModel.trySendAction(ChooseAuthOptionScreenActions.OnSelectPasscode)
                    },
                )

                when (state.dialogBoxType) {
                    DialogBoxType.ERROR -> {
                        MessageDialogBox(
                            onDismissRequest = {
                                viewModel.trySendAction(
                                    ChooseAuthOptionScreenActions.DismissDialogBox,
                                )
                            },
                            dialogMessage = state.dialogBoxMessage,
                        )
                    }
                    DialogBoxType.NOT_SET -> {
                        MessageDialogBox(
                            onDismissRequest = {
                                viewModel.trySendAction(
                                    ChooseAuthOptionScreenActions.SetupPlatformAuthenticator(platformAuthenticationProvider),
                                )
                            },
                            dialogMessage = state.dialogBoxMessage,
                        )
                    }
                    DialogBoxType.NOT_AVAILABLE -> {
                        MessageDialogBox(
                            onDismissRequest = {
                                viewModel.trySendAction(
                                    ChooseAuthOptionScreenActions.DismissDialogBox,
                                )
                            },
                            dialogMessage = state.dialogBoxMessage,
                        )
                    }
                    DialogBoxType.None -> {}
                }
            }

            Button(
                onClick = {
                    navigationHelper(
                        state.selectedAuthOption,
                        whenDeviceLockSelected = {
                            platformAuthenticationProvider.updateAuthenticatorStatus()
                            viewModel.trySendAction(
                                ChooseAuthOptionScreenActions.RegisterUserBiometrics(platformAuthenticationProvider),
                            )
                        },
                        whenPasscodeSelected = {
                            platformAuthenticationProvider.updateAuthenticatorStatus()
                            viewModel.trySendAction(
                                ChooseAuthOptionScreenActions.OnSelectPasscode,
                            )
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
fun MessageDialogBox(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    dialogMessage: String = "Coming Soon",
    dismissButtonText: String = "OK",
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = dialogMessage,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.height(12.dp))

                DialogButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.padding(end = 8.dp),
                    text = dismissButtonText,
                )
            }
        }
    }
}

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = blueTint,
            contentColor = White,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = White,
        ),
    ) {
        Text(text = text)
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
