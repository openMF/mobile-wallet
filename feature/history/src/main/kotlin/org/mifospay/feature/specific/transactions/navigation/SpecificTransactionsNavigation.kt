/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
@file:Suppress("MaxLineLength")

package org.mifospay.feature.specific.transactions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifospay.core.model.domain.Transaction
import org.mifospay.common.Constants
import org.mifospay.feature.specific.transactions.SpecificTransactionsScreen

const val SPECIFIC_TRANSACTIONS_ROUTE = "specific_transactions_route"

fun NavGraphBuilder.specificTransactionsScreen(
    onBackClick: () -> Unit,
    onTransactionItemClicked: (String) -> Unit,
) {
    composable(
        route = "$SPECIFIC_TRANSACTIONS_ROUTE?${Constants.ACCOUNT_NUMBER}={accountNumber}&${Constants.TRANSACTIONS}={transactions}",
        arguments = listOf(
            navArgument(Constants.ACCOUNT_NUMBER) { type = NavType.StringType },
            navArgument(Constants.TRANSACTIONS) { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        val accountNumber = backStackEntry.arguments?.getString(Constants.ACCOUNT_NUMBER) ?: ""
        val transactions =
            backStackEntry.arguments?.getParcelableArrayList<Transaction>(Constants.TRANSACTIONS)
                ?: arrayListOf()

        SpecificTransactionsScreen(
            accountNumber = accountNumber,
            transactions = transactions.toList(),
            backPress = onBackClick,
            transactionItemClicked = onTransactionItemClicked,
        )
    }
}

fun NavController.navigateToSpecificTransactions(
    accountNumber: String,
    transactions: ArrayList<Transaction>,
) {
    this.navigate("$SPECIFIC_TRANSACTIONS_ROUTE?${Constants.ACCOUNT_NUMBER}=$accountNumber&${Constants.TRANSACTIONS}=$transactions")
}
