/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.success

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class TransferSuccessRoute(
    val returnDestination: String = "home",
)

fun NavGraphBuilder.transferSuccessScreen(
    navigateBack: (String) -> Unit,
) {
    composable<TransferSuccessRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<TransferSuccessRoute>()
        TransferSuccessScreen(
            navigateBack = { navigateBack(route.returnDestination) },
        )
    }
}

fun NavController.navigateTransferSuccess(
    returnDestination: String = "home",
    navOptions: NavOptions? = null,
) {
    navigate(
        TransferSuccessRoute(returnDestination = returnDestination),
        navOptions,
    )
}
