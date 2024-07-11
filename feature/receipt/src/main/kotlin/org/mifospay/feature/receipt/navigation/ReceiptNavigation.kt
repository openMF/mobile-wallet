package org.mifospay.feature.receipt.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.receipt.ReceiptScreenRoute

const val RECEIPT_ROUTE = "receipt_route"

fun NavGraphBuilder.receiptScreen(
    onShowSnackbar: (String, String?) -> Boolean,
    openPassCodeActivity: (Uri) -> Unit,
    onBackClick: () -> Unit
) {
    composable(
        route = "$RECEIPT_ROUTE?uri={uri}",
        arguments = listOf(
            navArgument("uri") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val uriString = backStackEntry.arguments?.getString("uri")
        val uri = if (uriString != null) Uri.parse(uriString) else null

        ReceiptScreenRoute(
            uri = uri,
            onShowSnackbar = onShowSnackbar,
            openPassCodeActivity = { uri?.let { openPassCodeActivity(it) } },
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToReceipt(uri: Uri) {
    this.navigate("$RECEIPT_ROUTE?uri=${Uri.encode(uri.toString())}")
}