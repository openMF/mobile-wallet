package org.mifos.mobilewallet.mifospay.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifos.mobilewallet.mifospay.home.screens.PaymentsRoute

const val PAYMENTS_ROUTE = "payments_route"

fun NavController.navigateToPayments(navOptions: NavOptions) = navigate(PAYMENTS_ROUTE, navOptions)

fun NavGraphBuilder.paymentsScreen(
    showQr: (String) -> Unit,
    onNewSI: () -> Unit
) {
    composable(route = PAYMENTS_ROUTE) {
        PaymentsRoute(
            showQr = showQr,
            onNewSI = onNewSI
        )
    }
}
