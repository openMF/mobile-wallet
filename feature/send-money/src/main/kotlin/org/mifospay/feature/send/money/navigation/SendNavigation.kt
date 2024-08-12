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
import androidx.navigation.compose.composable
import org.mifospay.feature.send.money.SendScreenRoute

const val SEND_MONEY_ROUTE = "send_money_route"

fun NavController.navigateToSendMoneyScreen(
    navOptions: NavOptions? = null,
) = navigate(SEND_MONEY_ROUTE, navOptions)

fun NavGraphBuilder.sendMoneyScreen(
    proceedWithMakeTransferFlow: (String, String?) -> Unit,
    onBackClick: () -> Unit,
) {
    composable(route = SEND_MONEY_ROUTE) {
        SendScreenRoute(
            showToolBar = true,
            onBackClick = onBackClick,
            proceedWithMakeTransferFlow = proceedWithMakeTransferFlow,
        )
    }
}
