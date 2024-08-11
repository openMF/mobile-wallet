/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.common.Constants
import org.mifospay.feature.bank.accounts.details.BankAccountDetailScreen

const val BANK_ACCOUNT_DETAIL_ROUTE = "bank_account_detail_route"

fun NavGraphBuilder.bankAccountDetailScreen(
    onSetupUpiPin: (BankAccountDetails, Int) -> Unit,
    onChangeUpiPin: (BankAccountDetails, Int) -> Unit,
    onForgotUpiPin: (BankAccountDetails, Int) -> Unit,
    onBackClick: (BankAccountDetails, Int) -> Unit,
) {
    composable(
        route = "$BANK_ACCOUNT_DETAIL_ROUTE/{${Constants.BANK_ACCOUNT_DETAILS}}/{${Constants.INDEX}}",
        arguments = listOf(
            navArgument(Constants.BANK_ACCOUNT_DETAILS) { type = NavType.StringType },
            navArgument(Constants.INDEX) { type = NavType.IntType },
        ),
    ) { backStackEntry ->
        val bankAccountDetails =
            backStackEntry.arguments?.getParcelable(Constants.BANK_ACCOUNT_DETAILS)
                ?: BankAccountDetails("", "", "", "", "")
        val index = backStackEntry.arguments?.getInt(Constants.INDEX) ?: 0

        BankAccountDetailScreen(
            bankAccountDetails = bankAccountDetails,
            onSetupUpiPin = { onSetupUpiPin(bankAccountDetails, index) },
            onChangeUpiPin = {
                if (bankAccountDetails.isUpiEnabled) {
                    onChangeUpiPin(bankAccountDetails, index)
                } else {
                    // TODO: Use global snackbar
                }
            },
            onForgotUpiPin = {
                if (bankAccountDetails.isUpiEnabled) {
                    onForgotUpiPin(bankAccountDetails, index)
                } else {
                    // TODO: Use global snackbar
                }
            },
            navigateBack = { onBackClick(bankAccountDetails, index) },
        )
    }
}

fun NavController.navigateToBankAccountDetail(
    bankAccountDetails: BankAccountDetails,
    index: Int,
    navOptions: NavOptions? = null,
) {
    this.navigate("$BANK_ACCOUNT_DETAIL_ROUTE/$bankAccountDetails/$index", navOptions)
}
