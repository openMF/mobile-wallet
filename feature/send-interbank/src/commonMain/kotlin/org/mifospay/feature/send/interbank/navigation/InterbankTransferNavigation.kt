/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.interbank.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.mifospay.feature.send.interbank.InterbankTransferFlowScreen

@Serializable
data class InterbankTransferRoute(
    val returnDestination: String = "home",
)

fun NavController.navigateToInterbankTransfer(
    returnDestination: String = "home",
    navOptions: NavOptions? = null,
) {
    this.navigate(InterbankTransferRoute(returnDestination = returnDestination), navOptions)
}

fun NavGraphBuilder.interbankTransferScreen(
    onBackClick: () -> Unit,
    onTransferSuccess: () -> Unit,
    onContactSupport: () -> Unit,
) {
    composable<InterbankTransferRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<InterbankTransferRoute>()
        InterbankTransferFlowScreen(
            onBackClick = onBackClick,
            onTransferSuccess = onTransferSuccess,
            onContactSupport = onContactSupport,
        )
    }
}
