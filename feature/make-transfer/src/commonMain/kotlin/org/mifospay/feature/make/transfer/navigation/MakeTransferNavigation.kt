/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.mifospay.core.ui.composableWithPushTransitions
import org.mifospay.feature.make.transfer.MakeTransferScreen

private const val MAKE_TRANSFER_ROUTE_BASE = "make_transfer_route"
internal const val TRANSFER_ARG = "paymentData"

private const val TRANSFER_BASE_ROUTE = "$MAKE_TRANSFER_ROUTE_BASE/{$TRANSFER_ARG}"

fun NavGraphBuilder.transferScreen(
    navigateBack: () -> Unit,
    onTransferSuccess: () -> Unit,
) {
    composableWithPushTransitions(
        route = TRANSFER_BASE_ROUTE,
        arguments = listOf(
            navArgument(TRANSFER_ARG) {
                type = NavType.StringType
            },
        ),
    ) {
        MakeTransferScreen(
            navigateBack = navigateBack,
            onTransferSuccess = onTransferSuccess,
        )
    }
}

fun NavController.navigateToTransferScreen(
    paymentData: String,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = "$MAKE_TRANSFER_ROUTE_BASE/$paymentData",
        navOptions = navOptions,
    )
}
