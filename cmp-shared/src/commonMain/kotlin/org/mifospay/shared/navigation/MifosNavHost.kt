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
import mobile_wallet.cmp_shared.generated.resources.feature_finance_accounts
import mobile_wallet.cmp_shared.generated.resources.feature_finance_beneficiaries
import mobile_wallet.feature.payments.generated.resources.Res
import mobile_wallet.feature.payments.generated.resources.feature_payments_history
import mobile_wallet.feature.payments.generated.resources.feature_payments_request
import mobile_wallet.feature.payments.generated.resources.feature_payments_send
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.mifos.feature.passcode.internalMifosPasscodeScreen
import org.mifos.feature.passcode.navigateToInternalMifosPasscodeScreen
import org.mifospay.core.data.repository.UserVerificationRepository
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.feature.accounts.AccountsScreen
import org.mifospay.feature.accounts.savingsaccount.SavingsAddEditType
import org.mifospay.feature.accounts.savingsaccount.addEditSavingAccountScreen
import org.mifospay.feature.accounts.savingsaccount.details.navigateToSavingAccountDetails
import org.mifospay.feature.accounts.savingsaccount.details.savingAccountDetailRoute
import org.mifospay.feature.accounts.savingsaccount.navigateToSavingAccountAddEdit
import org.mifospay.feature.beneficiary.addupdatebeneficiary.BeneficiaryAddEditType
import org.mifospay.feature.beneficiary.addupdatebeneficiary.addEditBeneficiaryScreen
import org.mifospay.feature.beneficiary.addupdatebeneficiary.navigateToBeneficiaryAddEdit
import org.mifospay.feature.beneficiary.list.BeneficiaryListScreen
import org.mifospay.feature.editpassword.navigation.editPasswordScreen
import org.mifospay.feature.editpassword.navigation.navigateToEditPassword
import org.mifospay.feature.faq.navigation.faqScreen
import org.mifospay.feature.faq.navigation.navigateToFAQ
import org.mifospay.feature.fastmpay.navigation.FAST_MPAY_ROUTE
import org.mifospay.feature.fastmpay.navigation.fastMpayScreen
import org.mifospay.feature.fastmpay.navigation.navigateToFastMpay
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
import org.mifospay.feature.payments.RequestScreen
import org.mifospay.feature.payments.paymentsScreen
import org.mifospay.feature.payments.selectTransferType.SelectTransferTypeScreen
import org.mifospay.feature.profile.navigation.navigateToProfile
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
import org.mifospay.feature.pocket.navigation.POCKET_DASHBOARD_ROUTE
import org.mifospay.feature.pocket.navigation.linkAccountScreen
import org.mifospay.feature.pocket.navigation.navigateToLinkAccount
import org.mifospay.feature.pocket.navigation.navigateToPocketDashboard
import org.mifospay.feature.pocket.navigation.pocketDashboardScreen
import org.mifospay.feature.upi.setup.navigation.setupUpiPinScreen
import org.mifospay.shared.ui.MifosAppState
import mobile_wallet.cmp_shared.generated.resources.Res as SharedRes

const val AUTHENTICATION_VERIFICATION_KEY = "org.mifospay.mifos.authentication_verification_success"

@Composable
internal fun MifosNavHost(
    appState: MifosAppState,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val userVerificationRepository = koinInject<UserVerificationRepository>()

    val paymentsTabContents = listOf(
        TabContent(stringResource(Res.string.feature_payments_send)) {
            SelectTransferTypeScreen(
                onIntraBankTransferClick = {
                    navController.navigateToIntraBankHub()
                },
                onInterBankTransferClick = {
                    navController.navigateToInterbankTransfer()
                },
            )
        },
        TabContent(stringResource(Res.string.feature_payments_request)) {
            RequestScreen(
                showQr = navController::navigateToMpayQrScreen,
            )
        },
        TabContent(stringResource(Res.string.feature_payments_history)) {
            HistoryScreen(
                viewTransferDetail = navController::navigateToTransactionDetail,
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
        TabContent(stringResource(SharedRes.string.feature_finance_accounts)) {
            AccountsScreen(
                onAddEditSavingsAccount = navController::navigateToSavingAccountAddEdit,
                onViewSavingAccountDetails = navController::navigateToSavingAccountDetails,
                onAddOrEditBeneficiary = navController::navigateToBeneficiaryAddEdit,
            )
        },

        TabContent(stringResource(SharedRes.string.feature_finance_beneficiaries)) {
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
        startDestination = POCKET_DASHBOARD_ROUTE,
        navController = navController,
        modifier = modifier,
    ) {
        internalMifosPasscodeScreen(
            onForgotButton = onClickLogout,
            onAuthenticationSuccess = { verificationKey ->
                userVerificationRepository.recordVerification()
                verificationKey?.let {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(it, true)
                }
                navController.popBackStack()
            },
            onAuthenticationFailed = { verificationKey ->
                verificationKey?.let {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(it, false)
                }
                navController.popBackStack()
            },
            onPasscodeChanged = {
                navController.popBackStack()
            },
            onDisableBiometrics = {
                navController.popBackStack()
            },
        )

        homeScreen(
            onNavigateBack = navController::popBackStack,
            onRequest = {
                navController.navigateToMpayQrScreen()
            },
            onPay = navController::navigateToTransferOptions,
            navigateToPocketDashboard = navController::navigateToPocketDashboard,
            navigateToTransactionDetail = navController::navigateToSpecificTransaction,
            navigateToAccountDetail = navController::navigateToSavingAccountDetails,
            navigateToHistory = navController::navigateToHistory,
        )

        settingsScreen(
            onBackPress = navController::navigateUp,
            onLogout = onClickLogout,
            navigateToPasscodeScreen = navController::navigateToInternalMifosPasscodeScreen,
            navigateToEditPasswordScreen = navController::navigateToEditPassword,
            navigateToFaqScreen = navController::navigateToFAQ,
            navigateToNotificationScreen = navController::navigateToNotification,
            navigateToProfile = navController::navigateToProfile,
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
            navigateBack = navController::navigateUp,
            onLinkBankAccount = {
                navController.navigateToSavingAccountAddEdit(SavingsAddEditType.AddItem)
            },
            showQrCode = navController::navigateToMpayQrScreen,
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

        addEditBeneficiaryScreen(
            navigateBack = navController::navigateUp,
            navigateToQrReaderScreen = navController::navigateToScanQr,
            navigateToIntraBankTransfer = { officeId, clientId, accountTypeId, accountId, amount, accountName, accountNo ->
                // Navigate to transfer confirm with full QR data
                navController.navigateToTransferConfirm(
                    toOfficeId = officeId,
                    toClientId = clientId,
                    toAccountTypeId = accountTypeId,
                    toAccountId = accountId,
                    amount = amount,
                    toAccountName = accountName,
                    toAccountNo = accountNo,
                    returnDestination = "home",
                )
            },
            navigateToInterbankTransfer = { accountNumber, recipientName ->
                // Navigate to interbank transfer with pre-filled data
                navController.navigateToInterbankTransfer(
                    phoneNumber = accountNumber,
                    recipientName = recipientName,
                    amount = "",
                )
            },
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
            onNavigateToAddBeneficiary = { beneficiaryData, sourceQrType, sourceQrData ->
                navController.navigateToBeneficiaryAddEdit(
                    BeneficiaryAddEditType.AddItem(
                        beneficiary = beneficiaryData,
                        sourceQrType = sourceQrType,
                        sourceQrData = sourceQrData,
                    ),
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
            onNavigateBack = {
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
            onTransferSuccess = { transferResult, returnDestination ->
                navController.navigateTransferSuccess(
                    transferResult = transferResult,
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
            navigateForPasscodeVerification = { verificationKey ->
                navController.navigateToInternalMifosPasscodeScreen(verificationKey)
            },
        )

        intraBankHubScreen(
            navigateToSelectAccountScreen = {
                navController.navigateToSelectAccountScreen()
            },
            navigateToBeneficiary = {
                navController.navigateToBeneficiaryAddEdit(
                    BeneficiaryAddEditType.AddItem(),
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
            navigateToHistory = navController::navigateToHistory,
            navigateToScanQr = navController::navigateToScanQr,
            navigateToRequestMoney = navController::navigateToMpayQrScreen,
            navigateToTransferBeneficiary = { beneficiary ->
                // Default office ID since beneficiary doesn't have office info
                navController.navigateToTransferConfirm(
                    toOfficeId = 1,
                    toClientId = beneficiary.id,
                    toAccountTypeId = beneficiary.accountType.id,
                    toAccountId = beneficiary.id.toInt(),
                    amount = 0,
                    toAccountName = beneficiary.name,
                    toAccountNo = beneficiary.accountNumber,
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
                // Route through FastMpay for processing (bank mismatch, beneficiary check, etc.)
                navController.navigateToFastMpay(
                    qrData = qrData,
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
                    BeneficiaryAddEditType.AddItem(it),
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

        pocketDashboardScreen(
            navigateBack = navController::navigateUp,
            navigateToLinkAccount = navController::navigateToLinkAccount,
            navigateToAccountDetails = navController::navigateToSavingAccountDetails,
        )

        linkAccountScreen(
            navigateBack = navController::navigateUp,
            onLinkSuccess = { navController.navigateToPocketDashboard() },
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
