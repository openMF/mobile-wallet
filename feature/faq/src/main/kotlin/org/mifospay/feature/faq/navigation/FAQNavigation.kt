package org.mifospay.feature.faq.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.faq.FaqScreenRoute

const val FAQ_ROUTE = "faq_route"

fun NavController.navigateToFAQ(navOptions: NavOptions? = null) {
    this.navigate(FAQ_ROUTE, navOptions)
}

fun NavGraphBuilder.faqScreen(
    navigateBack: () -> Unit
) {
    composable(route = FAQ_ROUTE) {
        FaqScreenRoute(
            navigateBack = navigateBack
        )
    }
}