/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.intrabank.selectScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class SelectAccountRoute(
    val returnDestination: String = "home",
)

fun NavController.navigateToSelectAccountScreen(
    returnDestination: String = "home",
    navOptions: NavOptions? = null,
) {
    this.navigate(SelectAccountRoute(returnDestination = returnDestination), navOptions)
}

fun NavGraphBuilder.selectAccountScreenDestination(
    navigateBack: () -> Unit,
    navigateToMakeTransferScreen: (
        toOfficeId: Int?,
        toClientId: Long?,
        toAccountTypeId: Int?,
        toAccountId: Int,
        amount: Int,
        accountName: String,
        accountNo: String,
        returnDestination: String,
    ) -> Unit,
) {
    composable<SelectAccountRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SelectAccountRoute>()
        SelectPayeeScreen(
            navigateToMakeTransferScreen = { toOfficeId, toClientId, toAccountTypeId, toAccountId, amount, accountName, accountNo ->
                navigateToMakeTransferScreen(toOfficeId, toClientId, toAccountTypeId, toAccountId, amount, accountName, accountNo, route.returnDestination)
            },
            navigateBack = navigateBack,
        )
    }
}
