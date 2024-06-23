package org.mifospay.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    onRequest: (String) -> Unit,
    onPay: () -> Unit
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(
            onRequest = onRequest,
            onPay = onPay
        )
    }
}
