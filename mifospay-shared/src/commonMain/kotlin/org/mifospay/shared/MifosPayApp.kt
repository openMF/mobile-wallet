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

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.LocalTimeZone
import org.mifospay.shared.MainUiState.Success
import org.mifospay.shared.navigation.MifosNavGraph.LOGIN_GRAPH
import org.mifospay.shared.navigation.MifosNavGraph.PASSCODE_GRAPH
import org.mifospay.shared.navigation.RootNavGraph
import org.mifospay.shared.ui.rememberMifosAppState

@Composable
fun MifosPaySharedApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor = koinInject(),
    timeZoneMonitor: TimeZoneMonitor = koinInject(),
) {
    MifosPayApp(modifier, networkMonitor, timeZoneMonitor)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun MifosPayApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    viewModel: MifosPayViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    val appState = rememberMifosAppState(
        windowSizeClass = calculateWindowSizeClass(),
        networkMonitor = networkMonitor,
        timeZoneMonitor = timeZoneMonitor,
    )

    val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

    val navDestination = when (uiState) {
        is MainUiState.Loading -> LOGIN_GRAPH
        is Success -> if ((uiState as Success).userData.authenticated) {
            PASSCODE_GRAPH
        } else {
            LOGIN_GRAPH
        }
    }

    CompositionLocalProvider(
        LocalTimeZone provides currentTimeZone,
    ) {
        MifosTheme {
            RootNavGraph(
                appState = appState,
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
}
