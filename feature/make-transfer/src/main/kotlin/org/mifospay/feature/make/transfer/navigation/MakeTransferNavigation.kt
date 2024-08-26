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
import com.mifos.library.material3.navigation.bottomSheet
import org.mifospay.common.PAYEE_EXTERNAL_ID_ARG
import org.mifospay.common.TRANSFER_AMOUNT_ARG
import org.mifospay.feature.make.transfer.MakeTransferScreenRoute

const val MAKE_TRANSFER_ROUTE_BASE = "make_transfer_route"
const val MAKE_TRANSFER_ROUTE = MAKE_TRANSFER_ROUTE_BASE +
    "?${PAYEE_EXTERNAL_ID_ARG}={$PAYEE_EXTERNAL_ID_ARG}" +
    "&${TRANSFER_AMOUNT_ARG}={$TRANSFER_AMOUNT_ARG}"

fun NavController.navigateToMakeTransferScreen(
    externalId: String? = null,
    transferAmount: String? = null,
    navOptions: NavOptions? = null,
) {
    val route = MAKE_TRANSFER_ROUTE_BASE + if (transferAmount != null) {
        "?${PAYEE_EXTERNAL_ID_ARG}=$externalId" +
            "&${TRANSFER_AMOUNT_ARG}=$transferAmount"
    } else {
        "?${PAYEE_EXTERNAL_ID_ARG}=$externalId" +
            "&${TRANSFER_AMOUNT_ARG}=${"0.0"}"
    }
    navigate(route, navOptions)
}

fun NavGraphBuilder.makeTransferScreen(
    onDismiss: () -> Unit,
) {
    bottomSheet(
        route = MAKE_TRANSFER_ROUTE,
        arguments = listOf(
            navArgument(PAYEE_EXTERNAL_ID_ARG) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            },
            navArgument(TRANSFER_AMOUNT_ARG) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            },
        ),
    ) {
        MakeTransferScreenRoute(
            onDismiss = onDismiss,
        )
    }
}
