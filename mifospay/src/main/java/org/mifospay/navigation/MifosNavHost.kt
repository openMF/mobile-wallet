package org.mifospay.navigation

import org.mifospay.feature.savedcards.navigation.addCardScreen
import org.mifospay.feature.savedcards.navigation.navigateToAddCard
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
import org.mifospay.editprofile.ui.EditProfileActivity
import org.mifospay.feature.make.transfer.navigation.makeTransferScreen
import org.mifospay.feature.make.transfer.navigation.navigateToMakeTransferScreen
import org.mifospay.feature.request.money.navigation.navigateToShowQrScreen
import org.mifospay.feature.request.money.navigation.showQrScreen
import org.mifospay.feature.settings.SettingsActivity
import org.mifospay.history.specific_transactions.ui.SpecificTransactionsActivity
import org.mifospay.home.navigation.HOME_ROUTE
import org.mifospay.home.navigation.financeScreen
import org.mifospay.home.navigation.homeScreen
import org.mifospay.home.navigation.paymentsScreen
import org.mifospay.home.navigation.profileScreen
import org.mifospay.merchants.navigation.merchantTransferScreen
import org.mifospay.payments.send.navigation.navigateToSendMoneyScreen
import org.mifospay.payments.send.navigation.sendMoneyScreen
import org.mifospay.payments.ui.SendActivity
import org.mifospay.receipt.ui.ReceiptActivity
import org.mifospay.settings.ui.SettingsActivity
import org.mifospay.standinginstruction.ui.NewSIActivity
import org.mifospay.savedcards.ui.AddCardDialog

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
            onAddBtn = {  navController.navigateToAddCard() }
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
            onSettings = { context.startActivitySettings() }
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
    }
}

fun Context.startActivityStandingInstruction() {
    startActivity(Intent(this, NewSIActivity::class.java))
}

fun Context.startActivityEditProfile() {
    startActivity(Intent(this, EditProfileActivity::class.java))
}

fun Context.startActivitySettings() {
    startActivity(Intent(this, SettingsActivity::class.java))
}

fun Context.startActivitySend() {
    startActivity(Intent(this, SendActivity::class.java))
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