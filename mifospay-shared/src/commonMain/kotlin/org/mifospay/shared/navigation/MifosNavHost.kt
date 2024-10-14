/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.feature.editpassword.navigation.editPasswordScreen
import org.mifospay.feature.editpassword.navigation.navigateToEditPassword
import org.mifospay.feature.faq.navigation.faqScreen
import org.mifospay.feature.faq.navigation.navigateToFAQ
import org.mifospay.feature.finance.FinanceScreenContents
import org.mifospay.feature.finance.navigation.financeScreen
import org.mifospay.feature.history.HistoryScreen
import org.mifospay.feature.history.navigation.historyNavigation
import org.mifospay.feature.history.navigation.navigateToSpecificTransaction
import org.mifospay.feature.history.navigation.navigateToTransactionDetail
import org.mifospay.feature.history.navigation.specificTransactionsScreen
import org.mifospay.feature.history.navigation.transactionDetailNavigation
import org.mifospay.feature.home.navigation.HOME_ROUTE
import org.mifospay.feature.home.navigation.homeScreen
import org.mifospay.feature.payments.PaymentsScreenContents
import org.mifospay.feature.payments.RequestScreen
import org.mifospay.feature.payments.paymentsScreen
import org.mifospay.feature.profile.navigation.profileNavGraph
import org.mifospay.feature.settings.navigation.settingsScreen
import org.mifospay.shared.ui.MifosAppState

@Composable
internal fun MifosNavHost(
    appState: MifosAppState,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    val paymentsTabContents = listOf(
        TabContent(PaymentsScreenContents.SEND.name) {
        },
        TabContent(PaymentsScreenContents.REQUEST.name) {
            RequestScreen(showQr = {})
        },
        TabContent(PaymentsScreenContents.HISTORY.name) {
            HistoryScreen(
                viewTransferDetail = navController::navigateToTransactionDetail,
            )
        },
        TabContent(PaymentsScreenContents.SI.name) {
        },
        TabContent(PaymentsScreenContents.INVOICES.name) {
        },
    )

    val tabContents = listOf(
        TabContent(FinanceScreenContents.ACCOUNTS.name) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Accounts Screen || TODO", modifier = Modifier.align(Alignment.Center))
            }
        },
        TabContent(FinanceScreenContents.CARDS.name) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cards Screen || TODO", modifier = Modifier.align(Alignment.Center))
            }
        },
        TabContent(FinanceScreenContents.MERCHANTS.name) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Merchants Screen || TODO", modifier = Modifier.align(Alignment.Center))
            }
        },
        TabContent(FinanceScreenContents.KYC.name) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("KYC Screen || TODO", modifier = Modifier.align(Alignment.Center))
            }
        },
    )

    NavHost(
        route = MifosNavGraph.MAIN_GRAPH,
        startDestination = HOME_ROUTE,
        navController = navController,
        modifier = modifier,
    ) {
        homeScreen(
            onNavigateBack = navController::popBackStack,
            onRequest = {},
            onPay = {},
            navigateToTransactionDetail = navController::navigateToSpecificTransaction,
        )

        settingsScreen(
            onBackPress = navController::navigateUp,
            onLogout = onClickLogout,
            onChangePasscode = {},
            navigateToEditPasswordScreen = navController::navigateToEditPassword,
            navigateToFaqScreen = navController::navigateToFAQ,
            navigateToNotificationScreen = {},
        )

        faqScreen(
            navigateBack = navController::navigateUp,
        )

        editPasswordScreen(
            navigateBack = navController::navigateUp,
            onLogOut = onClickLogout,
        )

        profileNavGraph(
            navController = navController,
            onLinkBankAccount = {},
        )

        historyNavigation(
            viewTransactionDetail = navController::navigateToTransactionDetail,
        )

        paymentsScreen(tabContents = paymentsTabContents)

        financeScreen(tabContents = tabContents)

        specificTransactionsScreen(
            navigateBack = navController::navigateUp,
            viewTransactionDetail = navController::navigateToTransactionDetail,
        )

        transactionDetailNavigation(
            navigateBack = navController::navigateUp,
        )
    }
}
