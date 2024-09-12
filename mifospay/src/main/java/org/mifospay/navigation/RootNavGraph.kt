/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mifos.passcode.utility.BioMetricUtil
import org.mifospay.ui.MifosApp
import org.mifospay.ui.MifosAppState

@Composable
internal fun RootNavGraph(
    appState: MifosAppState,
    navHostController: NavHostController,
    startDestination: String,
    bioMetricUtil: BioMetricUtil,
    enableBiometric: Boolean,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = MifosNavGraph.ROOT_GRAPH,
        modifier = modifier,
    ) {
        loginNavGraph(navHostController)

        passcodeNavGraph(
            navHostController,
            bioMetricUtil = bioMetricUtil,
            enableBiometric = enableBiometric,
        )

        composable(MifosNavGraph.MAIN_GRAPH) {
            MifosApp(
                appState = appState,
                onClickLogout = onClickLogout,
            )
        }
    }
}
