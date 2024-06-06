package org.mifospay.payments.send.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.payments.send.SendScreenRoute


const val PAYEE_EXTERNAL_ID_ARG = "payeeExternalId"
const val TRANSFER_AMOUNT_ARG = "transferAmount"
const val SEND_MONEY_ROUTE = "send_money_route"

fun NavController.navigateToSendMoneyScreen(
    navOptions: NavOptions? = null
) = navigate(SEND_MONEY_ROUTE, navOptions)

fun NavGraphBuilder.sendMoneyScreen(
    proceedWithMakeTransferFlow: (String, String?) -> Unit,
    onBackClick: () -> Unit
) {
    composable(route = SEND_MONEY_ROUTE) {
        SendScreenRoute(
            showToolBar = true,
            onBackClick = onBackClick,
            proceedWithMakeTransferFlow = proceedWithMakeTransferFlow
        )
    }
}
