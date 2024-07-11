package org.mifospay.feature.invoices.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.invoices.InvoiceDetailScreen

const val INVOICE_ROUTE = "invoice_route"
const val INVOICE_DATA_ARG = "invoiceData"

fun NavController.navigateToInvoiceDetail(invoiceData: String) {
    this.navigate("$INVOICE_ROUTE/${Uri.encode(invoiceData)}")
}

fun NavGraphBuilder.invoiceDetailScreen(
    navigateToReceiptScreen: (String) -> Unit,
    onBackPress: () -> Unit
) {
    composable(
        route = "$INVOICE_ROUTE/{$INVOICE_DATA_ARG}",
        arguments = listOf(
            navArgument(INVOICE_DATA_ARG) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val invoiceData = Uri.decode(backStackEntry.arguments?.getString(INVOICE_DATA_ARG))
        InvoiceDetailScreen(
            data = Uri.parse(invoiceData),
            onBackPress = onBackPress,
            navigateToReceiptScreen = navigateToReceiptScreen
        )
    }
}