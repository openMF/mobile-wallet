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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.feature.accounts.AccountsScreen
import org.mifospay.feature.accounts.beneficiary.BeneficiaryAddEditType
import org.mifospay.feature.accounts.beneficiary.addEditBeneficiaryScreen
import org.mifospay.feature.accounts.beneficiary.navigateToBeneficiaryAddEdit
import org.mifospay.feature.accounts.benficiaryList.BeneficiaryListScreen
import org.mifospay.feature.accounts.savingsaccount.SavingsAddEditType
import org.mifospay.feature.accounts.savingsaccount.addEditSavingAccountScreen
import org.mifospay.feature.accounts.savingsaccount.details.navigateToSavingAccountDetails
import org.mifospay.feature.accounts.savingsaccount.details.savingAccountDetailRoute
import org.mifospay.feature.accounts.savingsaccount.navigateToSavingAccountAddEdit
import org.mifospay.feature.editpassword.navigation.editPasswordScreen
import org.mifospay.feature.editpassword.navigation.navigateToEditPassword
import org.mifospay.feature.faq.navigation.faqScreen
import org.mifospay.feature.faq.navigation.navigateToFAQ
import org.mifospay.feature.fastmpay.navigation.FAST_MPAY_ROUTE
import org.mifospay.feature.fastmpay.navigation.fastMpayScreen
import org.mifospay.feature.finance.FinanceScreenContents
import org.mifospay.feature.finance.navigation.FINANCE_ROUTE
import org.mifospay.feature.finance.navigation.financeScreen
import org.mifospay.feature.history.HistoryScreen
import org.mifospay.feature.history.navigation.historyNavigation
import org.mifospay.feature.history.navigation.navigateToHistory
import org.mifospay.feature.history.navigation.navigateToSpecificTransaction
import org.mifospay.feature.history.navigation.navigateToTransactionDetail
import org.mifospay.feature.history.navigation.specificTransactionsScreen
import org.mifospay.feature.history.navigation.transactionDetailNavigation
import org.mifospay.feature.home.navigation.HOME_ROUTE
import org.mifospay.feature.home.navigation.homeScreen
import org.mifospay.feature.invoices.navigation.invoiceDetailScreen
import org.mifospay.feature.kyc.navigation.kycLevel1Screen
import org.mifospay.feature.kyc.navigation.kycLevel2Screen
import org.mifospay.feature.kyc.navigation.kycLevel3Screen
import org.mifospay.feature.kyc.navigation.navigateToKYCLevel2
import org.mifospay.feature.kyc.navigation.navigateToKYCLevel3
import org.mifospay.feature.merchants.navigation.merchantTransferScreen
import org.mifospay.feature.mpay.qr.navigation.mpayQrScreen
import org.mifospay.feature.mpay.qr.navigation.navigateToMpayQrScreen
import org.mifospay.feature.mpay.qr.scan.navigation.SCAN_QR_ROUTE
import org.mifospay.feature.mpay.qr.scan.navigation.navigateToScanQr
import org.mifospay.feature.mpay.qr.scan.navigation.scanQrScreen
import org.mifospay.feature.notification.navigateToNotification
import org.mifospay.feature.notification.notificationScreen
import org.mifospay.feature.payments.PAYMENTS_ROUTE
import org.mifospay.feature.payments.PaymentsScreenContents
import org.mifospay.feature.payments.RequestScreen
import org.mifospay.feature.payments.paymentsScreen
import org.mifospay.feature.payments.selectTransferType.SelectTransferTypeScreen
import org.mifospay.feature.profile.navigation.profileNavGraph
import org.mifospay.feature.receipt.navigation.receiptScreen
import org.mifospay.feature.savedcards.createOrUpdate.addEditCardScreen
import org.mifospay.feature.savedcards.details.cardDetailRoute
import org.mifospay.feature.settings.navigation.settingsScreen
import org.mifospay.feature.standing.instruction.createOrUpdate.addEditSIScreen
import org.mifospay.feature.standing.instruction.details.siDetailsScreen
import org.mifospay.feature.transfer.interbank.navigation.interbankTransferScreen
import org.mifospay.feature.transfer.interbank.navigation.navigateToInterbankTransfer
import org.mifospay.feature.transfer.intrabank.navigation.intraBankHubScreen
import org.mifospay.feature.transfer.intrabank.navigation.navigateToIntraBankHub
import org.mifospay.feature.transfer.intrabank.navigation.navigateToTransferConfirm
import org.mifospay.feature.transfer.intrabank.navigation.transferConfirmScreen
import org.mifospay.feature.transfer.intrabank.selectScreen.navigateToSelectAccountScreen
import org.mifospay.feature.transfer.intrabank.selectScreen.selectAccountScreenDestination
import org.mifospay.feature.transfer.intrabank.success.navigateTransferSuccess
import org.mifospay.feature.transfer.intrabank.success.transferSuccessScreen
import org.mifospay.feature.upi.setup.navigation.setupUpiPinScreen
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
            SelectTransferTypeScreen(
                onIntraBankTransferClick = {
                    navController.navigateToIntraBankHub()
                },
                onInterBankTransferClick = {
                    navController.navigateToInterbankTransfer()
                },
            )
        },
        TabContent(PaymentsScreenContents.REQUEST.name) {
            RequestScreen(
                showQr = navController::navigateToMpayQrScreen,
            )
        },
        TabContent(PaymentsScreenContents.HISTORY.name) {
            HistoryScreen(
                viewTransferDetail = navController::navigateToTransactionDetail,
                showTopBar = false,
            )
        },
//        TabContent(PaymentsScreenContents.SI.name) {
//            StandingInstructionsScreen(
//                onAddEditSI = navController::navigateToSIAddEdit,
//                onShowSIDetails = navController::navigateSIDetails,
//            )
//        },
//        TabContent(PaymentsScreenContents.INVOICES.name) {
//            InvoiceScreen(
//                navigateToInvoiceDetailScreen = navController::navigateToInvoiceDetail,
//            )
//        },
    )

//    TODO Cards and Merchants are not using self api
    val tabContents = listOf(
        TabContent(FinanceScreenContents.ACCOUNTS.name) {
            AccountsScreen(
                onAddEditSavingsAccount = navController::navigateToSavingAccountAddEdit,
                onViewSavingAccountDetails = navController::navigateToSavingAccountDetails,
                onAddOrEditBeneficiary = navController::navigateToBeneficiaryAddEdit,
            )
        },

        TabContent(FinanceScreenContents.BENEFICIARIES.name) {
            BeneficiaryListScreen(
                onAddOrEditBeneficiary = navController::navigateToBeneficiaryAddEdit,
            )
        },
//        TabContent(FinanceScreenContents.CARDS.name) {
//            CardsScreen(
//                navigateToViewDetail = navController::navigateToCardDetails,
//                navigateToAddEdit = navController::navigateToCardAddEdit,
//            )
//        },
//        TabContent(FinanceScreenContents.MERCHANTS.name) {
//            MerchantScreen()
//        },
//        TabContent(FinanceScreenContents.KYC.name) {
//            KYCScreen(
//                onLevel1Clicked = navController::navigateToKYCLevel1,
//                onLevel2Clicked = navController::navigateToKYCLevel2,
//                onLevel3Clicked = navController::navigateToKYCLevel3,
//            )
//        },
    )

    NavHost(
        route = MifosNavGraph.MAIN_GRAPH,
        startDestination = HOME_ROUTE,
        navController = navController,
        modifier = modifier,
    ) {
        homeScreen(
            onNavigateBack = navController::popBackStack,
            onRequest = {
                navController.navigateToMpayQrScreen()
            },
            onPay = navController::navigateToTransferOptions,
            navigateToTransactionDetail = navController::navigateToSpecificTransaction,
            navigateToAccountDetail = navController::navigateToSavingAccountDetails,
            navigateToHistory = navController::navigateToHistory,
        )

        settingsScreen(
            onBackPress = navController::navigateUp,
            onLogout = onClickLogout,
            onChangePasscode = {},
            navigateToEditPasswordScreen = navController::navigateToEditPassword,
            navigateToFaqScreen = navController::navigateToFAQ,
            navigateToNotificationScreen = navController::navigateToNotification,
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
            onLinkBankAccount = {
                navController.navigateToSavingAccountAddEdit(SavingsAddEditType.AddItem)
            },
            showQrCode = navController::navigateToMpayQrScreen,
        )

        historyNavigation(
            viewTransactionDetail = navController::navigateToTransactionDetail,
            onBackClick = navController::navigateUp,
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

        addEditBeneficiaryScreen(
            navigateBack = navController::navigateUp,
            navigateToQrReaderScreen = navController::navigateToScanQr,
        )

        savingAccountDetailRoute(
            navigateBack = navController::navigateUp,
            onViewTransaction = navController::navigateToSpecificTransaction,
        )

        addEditSavingAccountScreen(
            navigateBack = navController::navigateUp,
        )

        invoiceDetailScreen(
            onNavigateBack = navController::navigateUp,
        )

        kycLevel1Screen(
            navigateBack = navController::navigateUp,
            navigateToKycLevel2 = {
                navController.navigateToKYCLevel2(
                    navOptions {
                        restoreState = true
                        popUpTo(FINANCE_ROUTE)
                    },
                )
            },
        )

        kycLevel2Screen(
            navigateBack = navController::navigateUp,
            navigateToLevel3 = {
                navController.navigateToKYCLevel3(
                    navOptions {
                        restoreState = true
                        popUpTo(FINANCE_ROUTE)
                    },
                )
            },
        )

        kycLevel3Screen(
            navigateBack = navController::navigateUp,
        )

        notificationScreen(
            navigateBack = navController::navigateUp,
        )

        cardDetailRoute(
            navigateBack = navController::navigateUp,
        )

        addEditCardScreen(
            navigateBack = navController::navigateUp,
        )

        receiptScreen(
            onBackClick = navController::navigateUp,
        )

        addEditSIScreen(
            navigateBack = navController::navigateUp,
        )

        siDetailsScreen(navigateBack = navController::navigateUp)

        mpayQrScreen(
            navigateBack = navController::navigateUp,
        )

        fastMpayScreen(
            onNavigateToAddBeneficiary = { beneficiaryData ->
                navController.navigateToBeneficiaryAddEdit(
                    BeneficiaryAddEditType.EditItem(beneficiaryData),
                    navOptions = navOptions {
                        popUpTo(FAST_MPAY_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
            onNavigateToMakeTransfer = { qrData, _ ->
                navController.navigateToTransferConfirm(
                    toOfficeId = qrData.officeId.toInt(),
                    toClientId = qrData.clientId,
                    toAccountTypeId = qrData.accountTypeId.toInt(),
                    toAccountId = qrData.accountId.toInt(),
                    amount = qrData.amount.toIntOrNull() ?: 0,
                    toAccountName = qrData.clientName,
                    toAccountNo = qrData.accountNo,
                    returnDestination = "home",
                    navOptions = navOptions {
                        popUpTo(FAST_MPAY_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
            onNavigateToInterbankTransfer = { accountExternalId, recipientName, amount ->
                navController.navigateToInterbankTransfer(
                    phoneNumber = accountExternalId,
                    recipientName = recipientName,
                    amount = amount,
                    navOptions = navOptions {
                        popUpTo(FAST_MPAY_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
            onNavigateToIntraBankTransfer = { qrData ->
                navController.navigateToTransferConfirm(
                    toOfficeId = qrData.officeId.toInt(),
                    toClientId = qrData.clientId,
                    toAccountTypeId = qrData.accountTypeId.toInt(),
                    toAccountId = qrData.accountId.toInt(),
                    amount = qrData.amount.toIntOrNull() ?: 0,
                    toAccountName = qrData.clientName,
                    toAccountNo = qrData.accountNo,
                    returnDestination = "home",
                    navOptions = navOptions {
                        popUpTo(FAST_MPAY_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
            onNavigateToMerchantPayment = { qrData ->
                // TODO: Navigate to merchant payment screen when implemented
                navController.navigateUp()
            },
            onError = { _ ->
                // Navigate back on error
                navController.navigateUp()
            },
        )

        selectAccountScreenDestination(
            navigateToTransferConfirm = navController::navigateToTransferConfirm,
            navigateBack = navController::popBackStack,
        )

        transferConfirmScreen(
            navigateBack = navController::popBackStack,
            onTransferSuccess = { returnDestination ->
                navController.navigateTransferSuccess(
                    returnDestination = returnDestination,
                    navOptions {
                        when (returnDestination) {
                            "payments" -> popUpTo(PAYMENTS_ROUTE) { inclusive = true }
                            else -> popUpTo(HOME_ROUTE) { inclusive = true }
                        }
                        launchSingleTop = true
                    },
                )
            },
        )

        intraBankHubScreen(
            navigateToSelectAccountScreen = {
                navController.navigateToSelectAccountScreen()
            },
            navigateToBeneficiary = {
                navController.navigateToBeneficiaryAddEdit(
                    BeneficiaryAddEditType.AddItem,
                )
            },
            navigateBack = navController::popBackStack,
            navigateToTransferConfirm = { toOfficeId, toClientId, toAccountId, accountName, accountNo ->
                navController.navigateToTransferConfirm(
                    toOfficeId = toOfficeId,
                    toClientId = toClientId,
                    // Savings account type
                    toAccountTypeId = 2,
                    toAccountId = toAccountId,
                    amount = 0,
                    toAccountName = accountName,
                    toAccountNo = accountNo,
                    returnDestination = "home",
                )
            },
        )

        transferSuccessScreen(
            navigateBack = { returnDestination ->
                when (returnDestination) {
                    "payments" -> {
                        navController.navigate(PAYMENTS_ROUTE) {
                            popUpTo(PAYMENTS_ROUTE) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }

                    else -> {
                        navController.navigate(HOME_ROUTE) {
                            popUpTo(HOME_ROUTE) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                }
            },
        )

        scanQrScreen(
            navigateBack = navController::popBackStack,
            navigateToIntraBankTransfer = { qrData ->
                navController.navigateToTransferConfirm(
                    toOfficeId = qrData.officeId.toInt(),
                    toClientId = qrData.clientId,
                    toAccountTypeId = qrData.accountTypeId.toInt(),
                    toAccountId = qrData.accountId.toInt(),
                    amount = qrData.amount.toIntOrNull() ?: 0,
                    toAccountName = qrData.clientName,
                    toAccountNo = qrData.accountNo,
                    returnDestination = "home",
                    navOptions = navOptions {
                        popUpTo(SCAN_QR_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
            navigateToInterbankTransfer = { accountExternalId, recipientName, amount ->
                navController.navigateToInterbankTransfer(
                    phoneNumber = accountExternalId,
                    recipientName = recipientName,
                    amount = amount,
                    navOptions = navOptions {
                        popUpTo(SCAN_QR_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
            navigateToAddBeneficiaryScreen = {
                navController.navigateToBeneficiaryAddEdit(
                    BeneficiaryAddEditType.EditItem(it),
                    navOptions = navOptions {
                        popUpTo(SCAN_QR_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
        )

        merchantTransferScreen(
            proceedWithMakeTransferFlow = { _, _ -> },
            onBackPressed = navController::navigateUp,
        )

        setupUpiPinScreen(
            navigateBack = navController::navigateUp,
        )

        transferOptionsDialog(
            onIntraBankTransferClick = navController::navigateToIntraBankHub,
            onInterBankTransferClick = navController::navigateToInterbankTransfer,
            onDismiss = {
                navController.popBackStack()
            },
        )

        interbankTransferScreen(
            onBackClick = navController::popBackStack,
            onTransferSuccess = {
                navController.navigate(HOME_ROUTE) {
                    popUpTo(HOME_ROUTE) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
        )
    }
}
