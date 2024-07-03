package org.mifospay.feature.finance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.finance.FinanceRoute

const val FINANCE_ROUTE = "finance_route"

fun NavController.navigateToFinance(navOptions: NavOptions) = navigate(FINANCE_ROUTE, navOptions)

fun NavGraphBuilder.financeScreen(
    onAddBtn: () -> Unit
) {
    composable(route = FINANCE_ROUTE) {
        FinanceRoute(
            onAddBtn = onAddBtn,
            onLevel1Clicked = {},
            onLevel2Clicked = {},
            onLevel3Clicked = {}
        )
    }
}
