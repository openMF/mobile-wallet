package org.mifospay.feature.merchants.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.merchants.ui.MerchantTransferScreenRoute

const val MERCHANT_TRANSFER_ROUTE = "merchant_transfer_route"

fun NavGraphBuilder.merchantTransferScreen(
    proceedWithMakeTransferFlow: (String, String?) -> Unit,
    onBackPressed: () -> Unit
) {
    composable(
        route = "$MERCHANT_TRANSFER_ROUTE/{merchantName}/{merchantVPA}/{merchantAccountNumber}",
        arguments = listOf(
            navArgument("merchantName") { type = NavType.StringType },
            navArgument("merchantVPA") { type = NavType.StringType },
            navArgument("merchantAccountNumber") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val merchantName = backStackEntry.arguments?.getString("merchantName") ?: ""
        val merchantVPA = backStackEntry.arguments?.getString("merchantVPA") ?: ""
        val merchantAccountNumber = backStackEntry.arguments?.getString("merchantAccountNumber") ?: ""

        MerchantTransferScreenRoute(
            onBackPressed = onBackPressed,
            merchantName = merchantName,
            merchantVPA = merchantVPA,
            merchantAccountNumber = merchantAccountNumber,
            proceedWithMakeTransferFlow = proceedWithMakeTransferFlow
        )
    }
}

fun NavController.navigateToMerchantTransferScreen(
    merchantName: String,
    merchantVPA: String,
    merchantAccountNumber: String
) {
    navigate("$MERCHANT_TRANSFER_ROUTE/$merchantName/$merchantVPA/$merchantAccountNumber")
}
