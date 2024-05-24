package org.mifospay.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import com.mifospay.core.model.domain.Transaction
import org.mifospay.common.Constants
import org.mifospay.editprofile.ui.EditProfileActivity
import org.mifospay.history.specific_transactions.ui.SpecificTransactionsActivity
import org.mifospay.home.navigation.HOME_ROUTE
import org.mifospay.home.navigation.financeScreen
import org.mifospay.home.navigation.homeScreen
import org.mifospay.home.navigation.paymentsScreen
import org.mifospay.home.navigation.profileScreen
import org.mifospay.payments.send.navigation.navigateToSendMoneyScreen
import org.mifospay.payments.send.navigation.sendMoneyScreen
import org.mifospay.payments.ui.SendActivity
import org.mifospay.qr.ui.ShowQrActivity
import org.mifospay.receipt.ui.ReceiptActivity
import org.mifospay.savedcards.ui.AddCardDialog
import org.mifospay.settings.ui.SettingsActivity
import org.mifospay.standinginstruction.ui.NewSIActivity
import org.mifospay.ui.MifosAppState

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun MifosNavHost(
    appState: MifosAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = HOME_ROUTE,
) {
    val navController = appState.navController
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen(
            onRequest = { vpa -> context.startActivityShowQr(vpa) },
            onPay = navController::navigateToSendMoneyScreen
        )
        paymentsScreen(
            showQr = { vpa -> context.startActivityShowQr(vpa) },
            onNewSI = { context.startActivityStandingInstruction() },
            onAccountClicked = { accountNo, transactionsList ->
                context.startActivitySpecificTransaction(accountNo = accountNo, transactionsList = transactionsList)
            },
            viewReceipt = { context.startActivityViewReceipt(it) }
        )
        financeScreen(
            onAddBtn = { context.startActivityAddCard() }
        )
        profileScreen(
            onEditProfile = { context.startActivityEditProfile() },
            onSettings = { context.startActivitySettings() }
        )
        sendMoneyScreen(
            onBackClick = navController::popBackStack
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

fun Context.startActivityShowQr(vpa: String) {
    startActivity(Intent(this, ShowQrActivity::class.java).apply {
        putExtra(Constants.QR_DATA, vpa)
    })
}

fun Context.startActivityAddCard() {
    startActivity(Intent(this, AddCardDialog::class.java))
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