/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.intrabank.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.mifospay.feature.send.intrabank.SendIntraBankScreen

@Serializable
data object SendIntraBankRoute

fun NavController.navigateToSendIntraBankScreen(navOptions: NavOptions? = null) {
    this.navigate(SendIntraBankRoute, navOptions)
}

fun NavGraphBuilder.sendIntraBankScreen(
    navigateToSelectAccountScreen: () -> Unit,
    navigateToBeneficiary: () -> Unit,
    navigateBack: () -> Unit,
    navigateToMakeTransfer: (
        toOfficeId: Int,
        toClientId: Long,
        toAccountId: Int,
        accountName: String,
        accountNo: String,
    ) -> Unit,
    showTopBar: Boolean = true,
) {
    composable<SendIntraBankRoute> {
        SendIntraBankScreen(
            navigateToSelectAccountScreen = navigateToSelectAccountScreen,
            navigateBack = navigateBack,
            navigateToBeneficiary = navigateToBeneficiary,
            navigateToMakeTransfer = navigateToMakeTransfer,
            showTopBar = showTopBar,
        )
    }
}
