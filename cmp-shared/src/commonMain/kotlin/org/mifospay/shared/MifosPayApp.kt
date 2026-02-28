/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.biometrics.PlatformAuthenticatorLocalCompositionProvider
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import org.mifos.authenticator.passcode.screen.PasscodeScreen
import org.mifos.feature.passcode.MifosPasscode
import org.mifos.feature.passcode.RE_AUTH_MIFOS_PASSCODE_ROUTE
import org.mifos.feature.passcode.ROOT_MIFOS_PASSCODE_ROUTE
import org.mifos.feature.passcode.navigateToReAuthMifosPasscodeScreen
import org.mifospay.core.common.GlobalAuthManager
import org.mifospay.core.data.util.AppLockOption
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.authenticator.biometrics.PLATFORM_AUTHENTICATOR_ROUTE
import org.mifospay.feature.authenticator.biometrics.navigateToPlatformAuthenticator
import org.mifospay.feature.authenticator.biometrics.navigateToReAuthPlatformAuthenticator
import org.mifospay.shared.MainUiState.Success
import org.mifospay.shared.navigation.MifosNavGraph.LOGIN_GRAPH
import org.mifospay.shared.navigation.RootNavGraph
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun MifosPaySharedApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor = koinInject(),
    timeZoneMonitor: TimeZoneMonitor = koinInject(),
) {
    PlatformAuthenticatorLocalCompositionProvider {

        MifosPayApp(modifier, networkMonitor, timeZoneMonitor)
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun MifosPayApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    viewModel: MifosPayViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    var showErrorDialog = remember { mutableStateOf<Boolean>(false) }
    val isUnauthorized by GlobalAuthManager.isUnauthorized.collectAsStateWithLifecycle()

    val lockTimeOut = 15_000L

    val onStopTime = remember { mutableStateOf(Long.MAX_VALUE) }

    LaunchedEffect(isUnauthorized) {
        if (isUnauthorized) {
            showErrorDialog.value = true
        }
    }

    if (showErrorDialog.value) {
        MifosDialogBox(
            title = "Unauthorized User",
            showDialogState = showErrorDialog.value,
            confirmButtonText = "Ok",
            onConfirm = {
                showErrorDialog.value = false
                viewModel.logOut()
                navController.navigate(LOGIN_GRAPH) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
                GlobalAuthManager.reset()
            },
            onDismiss = {},
            message = "Please login again to continue",
        )
    }

    val authOption = remember(uiState) {
        if (uiState is Success && (uiState as Success).userData.authenticated) {
            viewModel.getAuthOption()
        } else {
            null
        }
    }

    LaunchedEffect(authOption) {
        if (authOption == AppLockOption.None) {
            viewModel.logOut()
            navController.navigate(LOGIN_GRAPH) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val navDestination = when (uiState) {
        is MainUiState.Loading -> LOGIN_GRAPH
        is Success -> if ((uiState as Success).userData.authenticated) {
            when (authOption) {
                AppLockOption.MifosPasscode -> ROOT_MIFOS_PASSCODE_ROUTE
                AppLockOption.DeviceLock -> PLATFORM_AUTHENTICATOR_ROUTE
                else -> LOGIN_GRAPH
            }
        } else {
            LOGIN_GRAPH
        }
    }

    val lifeCycleObserver = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifeCycleObserver) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_START -> {}
                Lifecycle.Event.ON_RESUME -> {
                    val inactiveTime = Clock.System.now().toEpochMilliseconds() - onStopTime.value
                    Logger.a { "inactiveTime: ${inactiveTime/1000}s" }
                    if(inactiveTime > lockTimeOut) {
                        when(authOption){
                            AppLockOption.MifosPasscode -> {
                                navController.navigateToReAuthMifosPasscodeScreen()
                            }
                            AppLockOption.DeviceLock -> {
                                navController.navigateToReAuthPlatformAuthenticator()
                            }
                            else -> {}
                        }
                    }
                    onStopTime.value = Long.MAX_VALUE;
                }
                Lifecycle.Event.ON_PAUSE -> {}
                Lifecycle.Event.ON_STOP -> {
                    onStopTime.value = Clock.System.now().toEpochMilliseconds()
                }
                Lifecycle.Event.ON_DESTROY -> {}
                else -> {}
            }
        }
        lifeCycleObserver.addObserver(observer)
        onDispose { lifeCycleObserver.removeObserver(observer) }


    }

    MifosTheme {
        RootNavGraph(
            networkMonitor = networkMonitor,
            timeZoneMonitor = timeZoneMonitor,
            navHostController = navController,
            startDestination = navDestination,
            modifier = modifier,
            onClickLogout = {
                viewModel.logOut()
                navController.navigate(LOGIN_GRAPH) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
        )
    }

}

