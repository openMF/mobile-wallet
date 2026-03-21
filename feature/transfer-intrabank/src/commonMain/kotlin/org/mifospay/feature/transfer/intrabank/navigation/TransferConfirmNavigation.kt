/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transfer.intrabank.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.mifospay.feature.transfer.intrabank.confirm.TransferConfirmScreen
import org.mifospay.feature.transfer.intrabank.confirm.TransferResult
import template.core.base.ui.composableWithSlideTransitions

@Serializable
data class TransferConfirmRoute(
    val amount: Int,
    val accountId: Long,
    val toOfficeId: Int? = null,
    val toClientId: Long? = null,
    val toAccountTypeId: Int? = null,
    val toAccountName: String = "",
    val toAccountNo: String = "",
    val returnDestination: String = "home",
)

fun NavController.navigateToTransferConfirm(
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
        TransferConfirmRoute(
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

fun NavGraphBuilder.transferConfirmScreen(
    navigateBack: () -> Unit,
    onTransferSuccess: (TransferResult, String) -> Unit,
    navigateForPasscodeVerification: (String) -> Unit,
) {
    composableWithSlideTransitions<TransferConfirmRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<TransferConfirmRoute>()
        TransferConfirmScreen(
            navigateBack = navigateBack,
            onTransferSuccess = { transferResult ->
                onTransferSuccess(transferResult, route.returnDestination)
            },
            navigateForPasscodeVerification = navigateForPasscodeVerification,
            entryStateHandle = backStackEntry.savedStateHandle,
        )
    }
}
