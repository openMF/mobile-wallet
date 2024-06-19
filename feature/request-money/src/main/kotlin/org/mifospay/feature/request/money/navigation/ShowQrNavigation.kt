package org.mifospay.feature.request.money.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.request.money.ShowQrScreenRoute

const val SHOW_QR_ROUTE = "show_qr_route"

fun NavGraphBuilder.showQrScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "$SHOW_QR_ROUTE/{vpa}",
        arguments = listOf(navArgument("vpa") { type = NavType.StringType })
    ) { backStackEntry ->
        val vpa = backStackEntry.arguments?.getString("vpa")
        ShowQrScreenRoute(backPress = onBackClick, vpa = vpa!!)
    }
}

fun NavController.navigateToShowQrScreen(vpa: String) {
    navigate("$SHOW_QR_ROUTE/{$vpa}")
}
