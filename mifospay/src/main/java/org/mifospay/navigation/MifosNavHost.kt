package org.mifospay.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifospay.core.model.domain.Transaction
import org.mifospay.common.Constants
import org.mifospay.feature.home.navigation.HOME_ROUTE
import org.mifospay.feature.home.navigation.homeScreen
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
import org.mifospay.feature.profile.edit.EditProfileActivity
import org.mifospay.feature.profile.navigation.profileScreen
import org.mifospay.feature.receipt.ReceiptActivity
import org.mifospay.feature.request.money.navigation.navigateToShowQrScreen
import org.mifospay.feature.request.money.navigation.showQrScreen
import org.mifospay.feature.savedcards.navigation.addCardScreen
import org.mifospay.feature.savedcards.navigation.navigateToAddCard
import org.mifospay.feature.settings.navigation.navigateToSettings
import org.mifospay.feature.settings.navigation.settingsScreen
import org.mifospay.feature.specific.transactions.SpecificTransactionsActivity
import org.mifospay.home.navigation.financeScreen
import org.mifospay.home.navigation.paymentsScreen
import org.mifospay.payments.send.navigation.navigateToSendMoneyScreen
import org.mifospay.payments.send.navigation.sendMoneyScreen
import org.mifospay.standinginstruction.ui.NewSIActivity

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
            onNewSI = { context.startActivityStandingInstruction() },
            onAccountClicked = { accountNo, transactionsList ->
                context.startActivitySpecificTransaction(
                    accountNo = accountNo,
                    transactionsList = transactionsList
                )
            },
            viewReceipt = { context.startActivityViewReceipt(it) },
            proceedWithMakeTransferFlow = { externalId, transferAmount ->
                navController.navigateToMakeTransferScreen(externalId, transferAmount)
            }
        )
        financeScreen(
            onAddBtn = { navController.navigateToAddCard() },
            onLevel1Clicked = { navController.navigateToKYCLevel1() },
            onLevel2Clicked = { navController.navigateToKYCLevel2() },
            onLevel3Clicked = { navController.navigateToKYCLevel3() }
        )
        addCardScreen(
            onDismiss = navController::popBackStack,
            onAddCard = {
                // Handle adding the card
                navController.popBackStack()
            }
        )
        profileScreen(
            onEditProfile = { context.startActivityEditProfile() },
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
        settingsScreen(onBackPress = navController::popBackStack)

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
    }
}

fun Context.startActivityStandingInstruction() {
    startActivity(Intent(this, NewSIActivity::class.java))
}

fun Context.startActivityEditProfile() {
    startActivity(Intent(this, EditProfileActivity::class.java))
}

fun Context.startActivityViewReceipt(transactionId: String) {
    startActivity(Intent(this, ReceiptActivity::class.java).apply {
        data = Uri.parse(Constants.RECEIPT_DOMAIN + transactionId)
    })
}

fun Context.startActivitySpecificTransaction(
    accountNo: String,
    transactionsList: ArrayList<Transaction>
) {
    startActivity(Intent(this, SpecificTransactionsActivity::class.java).apply {
        putParcelableArrayListExtra(Constants.TRANSACTIONS, transactionsList)
        putExtra(Constants.ACCOUNT_NUMBER, accountNo)
    })
}