package org.mifospay.feature.finance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.feature.finance.FinanceRoute

const val FINANCE_ROUTE = "finance_route"

fun NavController.navigateToFinance(navOptions: NavOptions) = navigate(FINANCE_ROUTE, navOptions)

fun NavGraphBuilder.financeScreen(
    onAddBtn: () -> Unit,
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit,
    navigateToBankAccountDetailScreen: (BankAccountDetails,Int) -> Unit,
    navigateToLinkBankAccountScreen: () -> Unit
) {
    composable(route = FINANCE_ROUTE) {
        FinanceRoute(
            onAddBtn = onAddBtn,
            onLevel1Clicked = onLevel1Clicked,
            onLevel2Clicked = onLevel2Clicked,
            onLevel3Clicked = onLevel3Clicked,
            navigateToBankAccountDetailScreen = navigateToBankAccountDetailScreen,
            navigateToLinkBankAccountScreen = navigateToLinkBankAccountScreen
        )
    }
}
