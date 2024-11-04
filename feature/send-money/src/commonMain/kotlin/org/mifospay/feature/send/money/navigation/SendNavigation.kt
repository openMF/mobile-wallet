/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.mifospay.core.ui.composableWithSlideTransitions
import org.mifospay.feature.send.money.SendMoneyScreen

const val SEND_MONEY_ROUTE = "send_money_route"
const val SEND_MONEY_ARG = "requestData"

const val SEND_MONEY_BASE_ROUTE = "$SEND_MONEY_ROUTE?$SEND_MONEY_ARG={$SEND_MONEY_ARG}"

fun NavController.navigateToSendMoneyScreen(
    navOptions: NavOptions? = null,
) = navigate(SEND_MONEY_ROUTE, navOptions)

fun NavGraphBuilder.sendMoneyScreen(
    onBackClick: () -> Unit,
    navigateToTransferScreen: (String) -> Unit,
    navigateToScanQrScreen: () -> Unit,
) {
    composableWithSlideTransitions(
        route = SEND_MONEY_BASE_ROUTE,
        arguments = listOf(
            navArgument(SEND_MONEY_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        SendMoneyScreen(
            onBackClick = onBackClick,
            navigateToTransferScreen = navigateToTransferScreen,
            navigateToScanQrScreen = navigateToScanQrScreen,
        )
    }
}

fun NavController.navigateToSendMoneyScreen(
    requestData: String,
    navOptions: NavOptions? = null,
) {
    val route = "$SEND_MONEY_ROUTE?$SEND_MONEY_ARG=$requestData"
    val options = navOptions ?: NavOptions.Builder()
        .setPopUpTo(SEND_MONEY_ROUTE, inclusive = true)
        .build()

    navigate(route, options)
}
