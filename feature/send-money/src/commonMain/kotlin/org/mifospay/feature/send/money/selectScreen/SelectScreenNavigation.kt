package org.mifospay.feature.send.money.selectScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.mifospay.core.ui.composableWithSlideTransitions
import selectScreen.SelectPayeeScreen

@Serializable
data object SelectAccountRoute

fun NavController.navigateToSelectAccountScreen(navOptions: NavOptions? = null) {
    this.navigate(SelectAccountRoute, navOptions)
}

fun NavGraphBuilder.selectAccountScreenDestination(
    navigateToMakeTransferV2Screen: (clientId: Long, clientName: String, accountNo: String, amount: Int, accountId: Long) -> Unit,
) {
    composable<SelectAccountRoute> {
        SelectPayeeScreen(
            navigateToMakeTransferV2Screen = navigateToMakeTransferV2Screen,
        )
    }
}