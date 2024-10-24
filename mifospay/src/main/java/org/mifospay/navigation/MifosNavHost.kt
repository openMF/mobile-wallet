/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.navigation.compose.NavHost
import com.mifos.library.material3.navigation.ModalBottomSheetLayout
import org.mifospay.common.Constants
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.feature.bank.accounts.AccountsScreen
import org.mifospay.feature.bank.accounts.navigation.bankAccountDetailScreen
import org.mifospay.feature.bank.accounts.navigation.linkBankAccountScreen
import org.mifospay.feature.bank.accounts.navigation.navigateToBankAccountDetail
import org.mifospay.feature.bank.accounts.navigation.navigateToLinkBankAccount
import org.mifospay.feature.editpassword.navigation.editPasswordScreen
import org.mifospay.feature.editpassword.navigation.navigateToEditPassword
import org.mifospay.feature.faq.navigation.faqScreen
import org.mifospay.feature.faq.navigation.navigateToFAQ
import org.mifospay.feature.finance.FinanceScreenContents
import org.mifospay.feature.finance.navigation.financeScreen
import org.mifospay.feature.history.HistoryScreen
import org.mifospay.feature.home.navigation.HOME_ROUTE
import org.mifospay.feature.home.navigation.homeScreen
import org.mifospay.feature.invoices.InvoiceScreenRoute
import org.mifospay.feature.invoices.navigation.invoiceDetailScreen
import org.mifospay.feature.invoices.navigation.navigateToInvoiceDetail
import org.mifospay.feature.kyc.KYCScreen
import org.mifospay.feature.kyc.navigation.kycLevel1Screen
import org.mifospay.feature.kyc.navigation.kycLevel2Screen
import org.mifospay.feature.kyc.navigation.kycLevel3Screen
import org.mifospay.feature.kyc.navigation.kycScreen
import org.mifospay.feature.kyc.navigation.navigateToKYCLevel1
import org.mifospay.feature.kyc.navigation.navigateToKYCLevel2
import org.mifospay.feature.kyc.navigation.navigateToKYCLevel3
import org.mifospay.feature.make.transfer.navigation.makeTransferScreen
import org.mifospay.feature.make.transfer.navigation.navigateToMakeTransferScreen
import org.mifospay.feature.merchants.navigation.merchantTransferScreen
import org.mifospay.feature.merchants.ui.MerchantScreen
import org.mifospay.feature.notification.notificationScreen
import org.mifospay.feature.payments.PaymentsScreenContents
import org.mifospay.feature.payments.RequestScreen
import org.mifospay.feature.payments.paymentsScreen
import org.mifospay.feature.profile.navigation.editProfileScreen
import org.mifospay.feature.profile.navigation.navigateToEditProfile
import org.mifospay.feature.profile.navigation.profileScreen
import org.mifospay.feature.read.qr.navigation.readQrScreen
import org.mifospay.feature.receipt.navigation.navigateToReceipt
import org.mifospay.feature.receipt.navigation.receiptScreen
import org.mifospay.feature.request.money.navigation.navigateToShowQrScreen
import org.mifospay.feature.request.money.navigation.showQrScreen
import org.mifospay.feature.savedcards.CardsScreen
import org.mifospay.feature.savedcards.navigation.addCardScreen
import org.mifospay.feature.search.searchScreen
import org.mifospay.feature.send.money.SendScreenRoute
import org.mifospay.feature.send.money.navigation.navigateToSendMoneyScreen
import org.mifospay.feature.send.money.navigation.sendMoneyScreen
import org.mifospay.feature.settings.navigation.settingsScreen
import org.mifospay.feature.specific.transactions.navigation.navigateToSpecificTransactions
import org.mifospay.feature.specific.transactions.navigation.specificTransactionsScreen
import org.mifospay.feature.standing.instruction.StandingInstructionsScreenRoute
import org.mifospay.feature.standing.instruction.navigateToNewSiScreen
import org.mifospay.feature.standing.instruction.newSiScreen
import org.mifospay.feature.standing.instruction.siDetailsScreen
import org.mifospay.feature.upiSetup.navigation.navigateToSetupUpiPin
import org.mifospay.feature.upiSetup.navigation.setupUpiPinScreen
import org.mifospay.ui.MifosAppState
import java.io.File
import java.util.Objects

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Suppress("MaxLineLength", "LongMethod")
@Composable
internal fun MifosNavHost(
    appState: MifosAppState,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    val tabContents = listOf(
        TabContent(FinanceScreenContents.ACCOUNTS.name) {
            AccountsScreen(
                navigateToBankAccountDetailScreen = navController::navigateToBankAccountDetail,
                navigateToLinkBankAccountScreen = navController::navigateToLinkBankAccount,
            )
        },
        TabContent(FinanceScreenContents.CARDS.name) {
            CardsScreen(onEditCard = {})
        },
        TabContent(FinanceScreenContents.MERCHANTS.name) {
            MerchantScreen()
        },
        TabContent(FinanceScreenContents.KYC.name) {
            KYCScreen(
                onLevel1Clicked = navController::navigateToKYCLevel1,
                onLevel2Clicked = navController::navigateToKYCLevel2,
                onLevel3Clicked = navController::navigateToKYCLevel3,
            )
        },
    )

    val paymentsTabContents = listOf(
        TabContent(PaymentsScreenContents.SEND.name) {
            SendScreenRoute(
                onBackClick = {},
                showToolBar = false,
                proceedWithMakeTransferFlow = navController::navigateToMakeTransferScreen,
            )
        },
        TabContent(PaymentsScreenContents.REQUEST.name) {
            RequestScreen(showQr = navController::navigateToShowQrScreen)
        },
        TabContent(PaymentsScreenContents.HISTORY.name) {
            HistoryScreen(
                accountClicked = navController::navigateToSpecificTransactions,
                viewReceipt = {
                    navController
                        .navigateToReceipt(Uri.parse(Constants.RECEIPT_DOMAIN + it))
                },
            )
        },
        TabContent(PaymentsScreenContents.SI.name) {
            StandingInstructionsScreenRoute(
                onNewSI = navController::navigateToNewSiScreen,
                onBackPress = navController::popBackStack,
            )
        },
        TabContent(PaymentsScreenContents.INVOICES.name) {
            InvoiceScreenRoute(
                navigateToInvoiceDetailScreen = {
                    navController.navigateToInvoiceDetail(it.toString())
                },
            )
        },
    )

    ModalBottomSheetLayout(
        bottomSheetNavigator = appState.bottomSheetNavigator,
        modifier = modifier,
    ) {
        NavHost(
            route = MifosNavGraph.MAIN_GRAPH,
            startDestination = HOME_ROUTE,
            navController = navController,
        ) {
            homeScreen(
                onRequest = navController::navigateToShowQrScreen,
                onPay = navController::navigateToSendMoneyScreen,
            )
            paymentsScreen(
                tabContents = paymentsTabContents,
            )
            financeScreen(
                tabContents = tabContents,
            )
            addCardScreen(
                onDismiss = navController::popBackStack,
                onAddCard = {
                    // Handle adding the cards
                    navController.popBackStack()
                },
            )
            profileScreen(onEditProfile = navController::navigateToEditProfile)

            sendMoneyScreen(
                onBackClick = navController::popBackStack,
                proceedWithMakeTransferFlow = navController::navigateToMakeTransferScreen,
            )
            makeTransferScreen(
                onDismiss = navController::popBackStack,
            )
            showQrScreen(
                onBackClick = navController::popBackStack,
            )
            merchantTransferScreen(
                proceedWithMakeTransferFlow = navController::navigateToMakeTransferScreen,
                onBackPressed = navController::popBackStack,
            )
            settingsScreen(
                onBackPress = navController::popBackStack,
                navigateToEditPasswordScreen = navController::navigateToEditPassword,
                onLogout = onClickLogout,
                onChangePasscode = {
                    // TODO:: Implement change passcode screen
                },
                navigateToFaqScreen = navController::navigateToFAQ,
            )

            kycScreen(
                onLevel1Clicked = navController::navigateToKYCLevel1,
                onLevel2Clicked = navController::navigateToKYCLevel2,
                onLevel3Clicked = navController::navigateToKYCLevel3,
            )
            kycLevel1Screen(
                navigateToKycLevel2 = navController::navigateToKYCLevel2,
            )
            kycLevel2Screen(
                onSuccessKyc2 = navController::navigateToKYCLevel3,
            )

            kycLevel3Screen()

            newSiScreen(onBackClick = navController::popBackStack)

            siDetailsScreen(
                onClickCreateNew = navController::navigateToNewSiScreen,
                onBackPress = navController::popBackStack,
            )

            editProfileScreen(
                onBackPress = navController::popBackStack,
                getUri = ::getUri,
            )

            faqScreen(
                navigateBack = navController::popBackStack,
            )
            readQrScreen(
                onBackClick = navController::popBackStack,
            )

            specificTransactionsScreen(
                onBackClick = navController::popBackStack,
                onTransactionItemClicked = { transactionId ->
                    navController.navigateToReceipt(Uri.parse(Constants.RECEIPT_DOMAIN + transactionId))
                },
            )
            invoiceDetailScreen(
                onBackPress = navController::popBackStack,
//                navigateToReceiptScreen = { uri ->
//                    navController.navigateToReceipt(Uri.parse(Constants.RECEIPT_DOMAIN + uri))
//                },
            )
            receiptScreen(
                openPassCodeActivity = {
                    // TODO: Implement Passcode Screen for Receipt
                },
                onBackClick = navController::popBackStack,
            )
            setupUpiPinScreen(
                onBackPress = navController::popBackStack,
            )

            bankAccountDetailScreen(
                onSetupUpiPin = { bankAccountDetails, index ->
                    navController.navigateToSetupUpiPin(bankAccountDetails, index, Constants.SETUP)
                },
                onChangeUpiPin = { bankAccountDetails, index ->
                    navController.navigateToSetupUpiPin(bankAccountDetails, index, Constants.CHANGE)
                },
                onForgotUpiPin = { bankAccountDetails, index ->
                    navController.navigateToSetupUpiPin(bankAccountDetails, index, Constants.FORGOT)
                },
                onBackClick = { bankAccountDetails, index ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Constants.UPDATED_BANK_ACCOUNT,
                        bankAccountDetails,
                    )
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Constants.INDEX,
                        index,
                    )
                    navController.popBackStack()
                },
            )
            linkBankAccountScreen(
                onBackClick = navController::popBackStack,
            )
            editPasswordScreen(
                onBackPress = navController::popBackStack,
                onCancelChanges = navController::popBackStack,
            )

            notificationScreen()

            searchScreen(onBackClick = navController::popBackStack)
        }
    }
}

fun getUri(context: Context, file: File): Uri {
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        org.mifospay.BuildConfig.APPLICATION_ID + ".provider",
        file,
    )
    return uri
}
