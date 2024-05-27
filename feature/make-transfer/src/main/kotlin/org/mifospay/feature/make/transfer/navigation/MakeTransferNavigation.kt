package org.mifospay.feature.make.transfer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.common.PAYEE_EXTERNAL_ID_ARG
import org.mifospay.common.TRANSFER_AMOUNT_ARG
import org.mifospay.feature.make.transfer.MakeTransferScreenRoute

const val MAKE_TRANSFER_ROUTE_BASE = "make_transfer_route"
const val MAKE_TRANSFER_ROUTE = MAKE_TRANSFER_ROUTE_BASE +
        "?${PAYEE_EXTERNAL_ID_ARG}={$PAYEE_EXTERNAL_ID_ARG}" +
        "&${TRANSFER_AMOUNT_ARG}={$TRANSFER_AMOUNT_ARG}"

fun NavController.navigateToMakeTransferScreen(
    externalId: String? = null,
    transferAmount: String? = null,
    navOptions: NavOptions? = null
) {
    val route = MAKE_TRANSFER_ROUTE_BASE + if (transferAmount != null) {
        "?${PAYEE_EXTERNAL_ID_ARG}=$externalId" +
                "&${TRANSFER_AMOUNT_ARG}=$transferAmount"
    } else {
        "?${PAYEE_EXTERNAL_ID_ARG}=$externalId" +
                "&${TRANSFER_AMOUNT_ARG}=${"0.0"}"
    }
    navigate(route, navOptions)
}

fun NavGraphBuilder.makeTransferScreen() {
    composable(
        route = MAKE_TRANSFER_ROUTE,
        arguments = listOf(
            navArgument(PAYEE_EXTERNAL_ID_ARG) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            },
            navArgument(TRANSFER_AMOUNT_ARG) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            }
        )
    ) {
        MakeTransferScreenRoute()
    }
}
