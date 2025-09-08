package org.mifospay.feature.send.money.v2

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SendMoneyRoute

fun NavController.navigateToSendMoneyV2Screen(navOptions: NavOptions? = null) {
    this.navigate(SendMoneyRoute, navOptions)
}

fun NavGraphBuilder.sendMoneyScreenDestination(
    navigateToSelectAccountScreen: () -> Unit,
    navigateBack:()->Unit,
) {
    composable<SendMoneyRoute> {
        SendMoneyv2Screen(
            navigateToSelectAccountScreen = navigateToSelectAccountScreen,
            navigateBack = navigateBack,
        )
    }
}