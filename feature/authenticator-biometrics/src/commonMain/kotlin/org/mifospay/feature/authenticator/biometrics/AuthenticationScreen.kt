/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.authenticator.biometrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import mobile_wallet.feature.authenticator_biometrics.generated.resources.Res
import mobile_wallet.feature.authenticator_biometrics.generated.resources.feature_authenticator_biometrics_error
import mobile_wallet.feature.authenticator_biometrics.generated.resources.feature_authenticator_biometrics_ok
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifos.authenticator.biometrics.platformAvailableAuthenticationOption
import org.mifos.authenticator.passcode.components.MifosIcon
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.authenticator.biometrics.components.SystemAuthenticatorButton
import template.core.base.designsystem.theme.KptTheme

internal object CurrentInfo : NavigationEventInfo()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthenticationScreen(
    onAuthenticationSuccess: () -> Unit,
    onForcedLogOut: () -> Unit,
    viewModel: AuthenticationScreenViewModel = koinViewModel(),
) {
    val navEventState = rememberNavigationEventState(
        currentInfo = CurrentInfo,
    )
    NavigationBackHandler(
        state = navEventState,
        isBackEnabled = true,
        onBackCancelled = { },
        onBackCompleted = { },
    )

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            AuthenticationScreenEvent.OnAuthenticationSuccess -> onAuthenticationSuccess()
            AuthenticationScreenEvent.OnForceLogout -> onForcedLogOut()
        }
    }

    AuthenticationContent(
        state = state,
        onAction = viewModel::trySendAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationContent(
    state: AuthenticationScreenState,
    onAction: (AuthenticationScreenAction) -> Unit,
) {
    val platformAvailableAuthenticationOption = platformAvailableAuthenticationOption.current
    val platformAuthOptions by platformAvailableAuthenticationOption.currentAuthOption.collectAsStateWithLifecycle()
    val platformAuthenticationProvider = platformAuthenticationProvider.current
    val authenticatorStatus by platformAuthenticationProvider.authenticatorStatus.collectAsStateWithLifecycle()

    LaunchedEffect(authenticatorStatus) {
        if (authenticatorStatus.contains(PlatformAuthenticatorStatus.NOT_SETUP)) {
            onAction(AuthenticationScreenAction.AuthenticatorStatusNotSetup)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(KptTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        MifosIcon(modifier = Modifier.fillMaxWidth())

        AuthenticationScreenDialogBox(
            state.screenState,
            onAction,
        )

        Spacer(Modifier.height(100.dp))

        SystemAuthenticatorButton(
            onClick = {
                onAction(AuthenticationScreenAction.OnClickAuthenticate(platformAuthenticationProvider))
            },
            platformAuthOptions = platformAuthOptions,
            authenticatorStatus = authenticatorStatus,
        )
    }
}

@Composable
fun AuthenticationScreenDialogBox(
    screenState: AuthenticationScreenState.ScreenState?,
    onAction: (AuthenticationScreenAction) -> Unit,
) {
    when (screenState) {
        is AuthenticationScreenState.ScreenState.Error -> {
            MifosDialogBox(
                title = stringResource(Res.string.feature_authenticator_biometrics_error),
                showDialogState = true,
                confirmButtonText = stringResource(Res.string.feature_authenticator_biometrics_ok),
                dismissButtonText = null,
                onConfirm = {
                    onAction(AuthenticationScreenAction.OnDismissDialog)
                },
                onDismiss = {
                    onAction(AuthenticationScreenAction.OnDismissDialog)
                },
                message = screenState.message,
            )
        }
        is AuthenticationScreenState.ScreenState.UserNotRegistered -> {
            MifosDialogBox(
                title = stringResource(Res.string.feature_authenticator_biometrics_error),
                showDialogState = true,
                confirmButtonText = stringResource(Res.string.feature_authenticator_biometrics_ok),
                dismissButtonText = "",
                onConfirm = {
                    onAction(AuthenticationScreenAction.OkayOnUserNotRegisteredError)
                },
                onDismiss = {
                    onAction(AuthenticationScreenAction.OkayOnUserNotRegisteredError)
                },
                message = screenState.message,
            )
        }
        null -> {}
    }
}
