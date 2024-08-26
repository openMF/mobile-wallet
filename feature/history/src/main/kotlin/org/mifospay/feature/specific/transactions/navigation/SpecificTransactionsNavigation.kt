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
import org.mifospay.feature.specific.transactions.SpecificTransactionsScreen

const val SPECIFIC_TRANSACTIONS_BASE_ROUTE = "specific_transactions_route"
const val ACCOUNT_NUMBER_ARG = "accountNumber"
const val TRANSACTIONS_ARG = "transactions"

const val SPECIFIC_TRANSACTIONS_ROUTE = SPECIFIC_TRANSACTIONS_BASE_ROUTE +
    "?${ACCOUNT_NUMBER_ARG}={$ACCOUNT_NUMBER_ARG}" +
    "&${TRANSACTIONS_ARG}={$TRANSACTIONS_ARG}"

fun NavGraphBuilder.specificTransactionsScreen(
    onBackClick: () -> Unit,
    onTransactionItemClicked: (String) -> Unit,
) {
    composable(
        route = SPECIFIC_TRANSACTIONS_ROUTE,
        arguments = listOf(
            navArgument(ACCOUNT_NUMBER_ARG) { type = NavType.StringType },
            navArgument(ACCOUNT_NUMBER_ARG) { type = NavType.StringType },
        ),
    ) {
        SpecificTransactionsScreen(
            backPress = onBackClick,
            transactionItemClicked = onTransactionItemClicked,
        )
    }
}

fun NavController.navigateToSpecificTransactions(
    accountNumber: String,
    transactions: ArrayList<Transaction>,
) {
    this.navigate("$SPECIFIC_TRANSACTIONS_BASE_ROUTE?${ACCOUNT_NUMBER_ARG}=$accountNumber&${TRANSACTIONS_ARG}=$transactions")
}
