/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.mifospay.feature.make.transfer.MakeTransferScreen

@Serializable
data class MakeTransferRoute(
    val amount: Int,
    val accountId: Long,
    val toOfficeId: Int? = null,
    val toClientId: Long? = null,
    val toAccountTypeId: Int? = null,
    val toAccountName: String = "",
    val toAccountNo: String = "",
    val returnDestination: String = "home",
)

fun NavController.navigateToMakeTransferScreen(
    toOfficeId: Int?,
    toClientId: Long?,
    toAccountTypeId: Int?,
    toAccountId: Int,
    amount: Int,
    toAccountName: String,
    toAccountNo: String,
    returnDestination: String = "home",
    navOptions: NavOptions? = null,
) {
    this.navigate(
        MakeTransferRoute(
            toOfficeId = toOfficeId,
            toClientId = toClientId,
            toAccountTypeId = toAccountTypeId,
            accountId = toAccountId.toLong(),
            amount = amount,
            toAccountName = toAccountName,
            toAccountNo = toAccountNo,
            returnDestination = returnDestination,
        ),
        navOptions,
    )
}

fun NavGraphBuilder.makeTransferScreen(
    navigateBack: () -> Unit,
    onTransferSuccess: (String) -> Unit,
) {
    composable<MakeTransferRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<MakeTransferRoute>()
        MakeTransferScreen(
            navigateBack = navigateBack,
            onTransferSuccess = { onTransferSuccess(route.returnDestination) },
        )
    }
}
