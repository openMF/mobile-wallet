package org.mifospay.feature.standing.instruction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val NEW_SI_ROUTE = "new_si_route"

fun NavController.navigateToNewSiScreen(
    navOptions: NavOptions? = null
) = navigate(NEW_SI_ROUTE, navOptions)

fun NavGraphBuilder.newSiScreen(
    onBackClick: () -> Unit
) {
    composable(route = NEW_SI_ROUTE) {
        NewSIScreenRoute(
            onBackPress = onBackClick
        )
    }
}