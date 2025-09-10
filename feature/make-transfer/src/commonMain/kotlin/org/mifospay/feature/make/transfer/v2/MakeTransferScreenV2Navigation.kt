/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.v2

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class MakeTransferScreenV2Route(
    val amount: Int,
    val accountId: Long,
    val toOfficeId: Int? = null,
    val toClientId: Long? = null,
    val toAccountTypeId: Int? = null,
    val toAccountName: String = "",
    val toAccountNo: String = "",
    val returnDestination: String = "home",
)

fun NavController.navigateToMakeTransferScreenV2(
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
        MakeTransferScreenV2Route(
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

fun NavGraphBuilder.makeTransferScreenV2(
    navigateBack: () -> Unit,
    onTransferSuccess: (String) -> Unit,
) {
    composable<MakeTransferScreenV2Route> { backStackEntry ->
        val route = backStackEntry.toRoute<MakeTransferScreenV2Route>()
        MakeTransferScreenV2(
            navigateBack = navigateBack,
            onTransferSuccess = { onTransferSuccess(route.returnDestination) },
        )
    }
}
