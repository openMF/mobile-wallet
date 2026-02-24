/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-passcode-cmp/blob/development/LICENSE
 */
package org.mifospay.feature.authenticator.biometrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.mifospay.feature.authenticator.biometrics.components.SystemAuthenticatorButton
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifos.authenticator.biometrics.platformAvailableAuthenticationOption
import org.mifos.authenticator.passcode.components.DialogButton
import org.mifos.authenticator.passcode.components.MifosIcon
import template.core.base.designsystem.theme.KptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    authenticationScreenViewModel: AuthenticationScreenViewModel = koinViewModel(),
    navController: NavController,
) {
    val verificationResult = authenticationScreenViewModel.authenticationResult.collectAsStateWithLifecycle()
    val platformAvailableAuthenticationOption = platformAvailableAuthenticationOption.current
    val platformAuthOptions by platformAvailableAuthenticationOption.currentAuthOption.collectAsStateWithLifecycle()
    val platformAuthenticationProvider = platformAuthenticationProvider.current
    val authenticatorStatus by platformAuthenticationProvider.authenticatorStatus.collectAsStateWithLifecycle()
    val isLoading by authenticationScreenViewModel.isLoading.collectAsStateWithLifecycle()

    var dialogBoxType by rememberSaveable {
        mutableStateOf(DialogBoxType.None)
    }

    var dialogMessage by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        if (authenticatorStatus.contains(PlatformAuthenticatorStatus.NOT_SETUP)) {
            authenticationScreenViewModel.clearUserRegistrationFromApp()
//            navController.popBackStack()
//            navController.navigate(Route.LoginScreen) {
//                popUpTo(0)
//            }
        }
    }

    LaunchedEffect(
        verificationResult.value,
    ) {
        when (verificationResult.value) {
            is AuthenticationResult.Error -> {
                dialogBoxType = DialogBoxType.ERROR
                dialogMessage = (verificationResult.value as AuthenticationResult.Error).message
                authenticationScreenViewModel.setAuthenticationResultNull()
            }
            is AuthenticationResult.Success -> {
//                navController.popBackStack()
//                navController.navigate(Route.HomeScreen) {
//                    popUpTo(0)
//                }
                authenticationScreenViewModel.setAuthenticationResultNull()
            }
            is AuthenticationResult.UserNotRegistered -> {
                dialogBoxType = DialogBoxType.NOT_SET
                dialogMessage = "The user has changed authentication settings, register again."
                authenticationScreenViewModel.clearUserRegistrationFromApp()
//                navController.popBackStack()
//                navController.navigate(Route.LoginScreen) {
//                    popUpTo(0)
//                }
                authenticationScreenViewModel.setAuthenticationResultNull()
            }
            null -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        MifosIcon(modifier = Modifier.fillMaxWidth())

        when (dialogBoxType) {
            DialogBoxType.ERROR -> {
                MessageDialogBox(
                    onDismissRequest = { dialogBoxType = DialogBoxType.None },
                    dialogMessage = dialogMessage,
                )
            }
            DialogBoxType.NOT_SET -> {
                MessageDialogBox(
                    onDismissRequest = {
                        authenticationScreenViewModel.clearUserRegistrationFromApp()
                        navController.popBackStack()
                        navController.navigate(Route.LoginScreen) {
                            popUpTo(0)
                        }
                    },
                    dialogMessage = dialogMessage,
                )
            }
            DialogBoxType.NOT_AVAILABLE -> {
                MessageDialogBox(
                    onDismissRequest = { dialogBoxType = DialogBoxType.None },
                    dialogMessage = dialogMessage,
                )
            }
            DialogBoxType.None -> {}
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            SystemAuthenticatorButton(
                onClick = {
                    platformAuthenticationProvider.updateAuthenticatorStatus()
                    authenticationScreenViewModel.authenticateUser(
                        "Mifos App",
                        platformAuthenticationProvider,
                    )
                },
                platformAuthOptions = platformAuthOptions,
                authenticatorStatus = authenticatorStatus,
            )
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
                .background(KptTheme.colorScheme.background)
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