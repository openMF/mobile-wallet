package org.mifospay.feature.upi_setup.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.common.Constants
import org.mifospay.feature.upi_setup.screens.SetupUpiPinScreenRoute

const val SETUP_UPI_PIN_ROUTE = "setup_upi_pin_route"

fun NavGraphBuilder.setupUpiPinScreen() {
    composable(
        route = "$SETUP_UPI_PIN_ROUTE/{${Constants.INDEX}}/{${Constants.TYPE}}",
        arguments = listOf(
            navArgument(Constants.INDEX) { type = NavType.IntType },
            navArgument(Constants.TYPE) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val bankAccountDetails =
            backStackEntry.arguments?.getParcelable(Constants.BANK_ACCOUNT_DETAILS)
                ?: BankAccountDetails("", "", "", "", "")
        val index = backStackEntry.arguments?.getInt(Constants.INDEX) ?: 0
        val type = backStackEntry.arguments?.getString(Constants.TYPE) ?: ""

        SetupUpiPinScreenRoute(
            bankAccountDetails = bankAccountDetails,
            type = type,
            index = index
        )
    }
}

fun NavController.navigateToSetupUpiPin(
    bankAccountDetails: BankAccountDetails,
    index: Int,
    type: String
) {
    val bundle = Bundle().apply {
        putParcelable(Constants.BANK_ACCOUNT_DETAILS, bankAccountDetails)
    }
    this.navigate("$SETUP_UPI_PIN_ROUTE/$index/$type") {
        this.launchSingleTop = true
        this.restoreState = true
    }
    currentBackStackEntry?.arguments?.putAll(bundle)
}