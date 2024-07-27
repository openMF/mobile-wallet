package org.mifospay.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifos.mobile.passcode.utils.PassCodeConstants
import org.mifospay.common.Constants
import org.mifospay.feature.auth.navigation.loginScreen
import org.mifospay.feature.auth.navigation.mobileVerificationScreen
import org.mifospay.feature.auth.navigation.navigateToMobileVerification
import org.mifospay.feature.auth.navigation.navigateToSignup
import org.mifospay.feature.auth.navigation.signupScreen
import org.mifospay.feature.bank.accounts.navigation.bankAccountDetailScreen
import org.mifospay.feature.bank.accounts.navigation.linkBankAccountScreen
import org.mifospay.feature.bank.accounts.navigation.navigateToBankAccountDetail
import org.mifospay.feature.bank.accounts.navigation.navigateToLinkBankAccount
import org.mifospay.feature.editpassword.navigation.editPasswordScreen
import org.mifospay.feature.editpassword.navigation.navigateToEditPassword
import org.mifospay.feature.faq.navigation.faqScreen
import org.mifospay.feature.finance.navigation.financeScreen
import org.mifospay.feature.home.navigation.HOME_ROUTE
import org.mifospay.feature.home.navigation.homeScreen
import org.mifospay.feature.invoices.navigation.invoiceDetailScreen
import org.mifospay.feature.invoices.navigation.navigateToInvoiceDetail
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
import org.mifospay.feature.passcode.PassCodeActivity
import org.mifospay.feature.payments.paymentsScreen
import org.mifospay.feature.profile.navigation.editProfileScreen
import org.mifospay.feature.profile.navigation.navigateToEditProfile
import org.mifospay.feature.profile.navigation.profileScreen
import org.mifospay.feature.read.qr.navigation.readQrScreen
import org.mifospay.feature.receipt.navigation.navigateToReceipt
import org.mifospay.feature.receipt.navigation.receiptScreen
import org.mifospay.feature.request.money.navigation.navigateToShowQrScreen
import org.mifospay.feature.request.money.navigation.showQrScreen
import org.mifospay.feature.savedcards.navigation.addCardScreen
import org.mifospay.feature.savedcards.navigation.navigateToAddCard
import org.mifospay.feature.send.money.navigation.navigateToSendMoneyScreen
import org.mifospay.feature.send.money.navigation.sendMoneyScreen
import org.mifospay.feature.settings.navigation.navigateToSettings
import org.mifospay.feature.settings.navigation.settingsScreen
import org.mifospay.feature.specific.transactions.navigation.navigateToSpecificTransactions
import org.mifospay.feature.specific.transactions.navigation.specificTransactionsScreen
import org.mifospay.feature.standing.instruction.navigateToNewSiScreen
import org.mifospay.feature.standing.instruction.newSiScreen
import org.mifospay.feature.upi_setup.navigation.navigateToSetupUpiPin
import org.mifospay.feature.upi_setup.navigation.setupUpiPinScreen
import java.io.File
import java.util.Objects

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun MifosNavHost(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = HOME_ROUTE,
) {

    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen(
            onRequest = { vpa -> navController.navigateToShowQrScreen(vpa) },
            onPay = navController::navigateToSendMoneyScreen
        )
        paymentsScreen(
            showQr = { vpa -> navController.navigateToShowQrScreen(vpa) },
            onNewSI = { navController.navigateToNewSiScreen() },
            onAccountClicked = { accountNo, transactionsList ->
                navController.navigateToSpecificTransactions(accountNo, transactionsList)
            },
            viewReceipt = { navController.navigateToReceipt(Uri.parse(Constants.RECEIPT_DOMAIN + it)) },
            proceedWithMakeTransferFlow = { externalId, transferAmount ->
                navController.navigateToMakeTransferScreen(externalId, transferAmount)
            },
            navigateToInvoiceDetailScreen = { uri ->
                navController.navigateToInvoiceDetail(uri.toString())
            }
        )
        financeScreen(
            onAddBtn = { navController.navigateToAddCard() },
            onLevel1Clicked = { navController.navigateToKYCLevel1() },
            onLevel2Clicked = { navController.navigateToKYCLevel2() },
            onLevel3Clicked = { navController.navigateToKYCLevel3() },
            navigateToBankAccountDetailScreen = { bankAccountDetails, index ->
                navController.navigateToBankAccountDetail(bankAccountDetails, index)
            },
            navigateToLinkBankAccountScreen = {
                navController.navigateToLinkBankAccount()
            }
        )
        addCardScreen(
            onDismiss = navController::popBackStack,
            onAddCard = {
                // Handle adding the card
                navController.popBackStack()
            }
        )
        profileScreen(
            onEditProfile = { navController.navigateToEditProfile() },
            onSettings = { navController.navigateToSettings() }
        )
        sendMoneyScreen(
            onBackClick = navController::popBackStack,
            proceedWithMakeTransferFlow = { externalId, transferAmount ->
                navController.navigateToMakeTransferScreen(externalId, transferAmount)
            }
        )
        makeTransferScreen(
            onDismiss = navController::popBackStack
        )
        showQrScreen(
            onBackClick = navController::popBackStack
        )
        merchantTransferScreen(
            proceedWithMakeTransferFlow = { externalId, transferAmount ->
                navController.navigateToMakeTransferScreen(externalId, transferAmount)
            },
            onBackPressed = navController::popBackStack
        )
        settingsScreen(
            onBackPress = navController::popBackStack,
            navigateToEditPasswordScreen = {
                navController.navigateToEditPassword()
            }
        )

        kycScreen(
            onLevel1Clicked = {
                // Navigate to Level 1 screen
                navController.navigate("kyc_level_1")
            },
            onLevel2Clicked = {
                // Navigate to Level 2 screen
                navController.navigate("kyc_level_2")
            },
            onLevel3Clicked = {
                // Navigate to Level 3 screen
                navController.navigate("kyc_level_3")
            }
        )
        kycLevel1Screen(
            navigateToKycLevel2 = {
                // Navigate to KYC Level 2 screen
                // For now, we'll just pop back to the previous screen
                navController.popBackStack()
            }
        )
        kycLevel2Screen(
            onSuccessKyc2 = {
                navController.popBackStack()
            }
        )
        kycLevel3Screen()
        newSiScreen(onBackClick = navController::popBackStack)

        editProfileScreen(
            onBackPress = navController::popBackStack,
            getUri = { context, file ->
                getUri(context, file)
            },
        )

        faqScreen(
            navigateBack = { navController.popBackStack() }
        )
        readQrScreen(
            onBackClick = navController::popBackStack
        )

        specificTransactionsScreen(
            onBackClick = navController::popBackStack,
            onTransactionItemClicked = { transactionId ->
                navController.navigateToReceipt(Uri.parse(Constants.RECEIPT_DOMAIN + transactionId))
            }
        )
        invoiceDetailScreen(
            onBackPress = { navController.popBackStack() },
            navigateToReceiptScreen = { uri ->
                navController.navigateToReceipt(Uri.parse(Constants.RECEIPT_DOMAIN + uri))
            }
        )
        receiptScreen(
            onShowSnackbar = { message, action ->
                //Todo: Use onShowSnackbar
                true
            },
            openPassCodeActivity = { uri ->
                context.openPassCodeActivity(uri)
            },
            onBackClick = navController::popBackStack
        )
        setupUpiPinScreen()
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
                    bankAccountDetails
                )
                navController.previousBackStackEntry?.savedStateHandle?.set(Constants.INDEX, index)
                navController.popBackStack()
            }
        )
        linkBankAccountScreen(
            onBackClick = { navController.popBackStack() }
        )
        editPasswordScreen(
            onBackPress = {
                navController.popBackStack()
            },
            onCancelChanges = {
                navController.popBackStack()
            }
        )
        signupScreen(
            onLoginSuccess = {
                val intent = Intent(context, PassCodeActivity::class.java).apply {
                    putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
                }
                context.startActivity(intent)
            },
            onRegisterSuccess = {
                //Todo: Implement onRegisterSuccess
            },
        )
        mobileVerificationScreen(
            onOtpVerificationSuccess = { fullNumber, extraData ->
                navController.navigateToSignup(
                    savingProductId = extraData[Constants.MIFOS_SAVINGS_PRODUCT_ID] as Int,
                    mobileNumber = fullNumber,
                    country = "Canada",
                    email = extraData[Constants.GOOGLE_EMAIL] as? String ?: "",
                    firstName = extraData[Constants.GOOGLE_GIVEN_NAME] as? String ?: "",
                    lastName = extraData[Constants.GOOGLE_FAMILY_NAME] as? String ?: "",
                    businessName = extraData[Constants.GOOGLE_DISPLAY_NAME] as? String ?: ""
                )
            }
        )

        loginScreen(
            onDismissSignUp = {
                //Todo: Navigate to the main screen after successful login
            },
            onNavigateToMobileVerificationScreen = { mifosSignedUp, googleDisplayName, googleEmail, googleFamilyName, googleGivenName ->
                navController.navigateToMobileVerification(
                    mifosSignedUp = mifosSignedUp,
                    googleDisplayName = googleDisplayName,
                    googleEmail = googleEmail,
                    googleFamilyName = googleFamilyName,
                    googleGivenName = googleGivenName
                )
            }
        )
    }
}

fun Context.openPassCodeActivity(deepLinkURI: Uri) {
    PassCodeActivity.startPassCodeActivity(
        context = this,
        bundle = bundleOf(
            Pair("uri", deepLinkURI.toString()),
            Pair(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
        ),
    )
}

fun getUri(context: Context, file: File): Uri {
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        org.mifospay.BuildConfig.APPLICATION_ID + ".provider", file
    )
    return uri
}