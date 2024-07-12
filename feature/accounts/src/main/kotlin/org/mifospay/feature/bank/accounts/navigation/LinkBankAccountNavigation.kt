package org.mifospay.feature.bank.accounts.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.bank.accounts.link.LinkBankAccountRoute

const val LINK_BANK_ACCOUNT_ROUTE = "link_bank_account_route"

fun NavController.navigateToLinkBankAccount(navOptions: NavOptions? = null) =
    navigate(LINK_BANK_ACCOUNT_ROUTE, navOptions)

fun NavGraphBuilder.linkBankAccountScreen(
    onBackClick: () -> Unit
) {
    composable(route = LINK_BANK_ACCOUNT_ROUTE) {
        LinkBankAccountRoute(
            onBackClick = onBackClick
        )
    }
}