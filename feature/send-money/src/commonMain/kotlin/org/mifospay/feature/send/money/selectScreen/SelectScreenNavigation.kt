/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money.selectScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SelectAccountRoute

fun NavController.navigateToSelectAccountScreen(navOptions: NavOptions? = null) {
    this.navigate(SelectAccountRoute, navOptions)
}

fun NavGraphBuilder.selectAccountScreenDestination(
    navigateBack: () -> Unit,
    navigateToMakeTransferV2Screen: (clientId: Long, clientName: String, accountNo: String, amount: Int, accountId: Long) -> Unit,
) {
    composable<SelectAccountRoute> {
        SelectPayeeScreen(
            navigateToMakeTransferV2Screen = navigateToMakeTransferV2Screen,
            navigateBack = navigateBack,
        )
    }
}
