/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.receipt.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.receipt.ReceiptScreenRoute

const val RECEIPT_ROUTE = "receipt_route"

fun NavGraphBuilder.receiptScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = RECEIPT_ROUTE,
    ) {
        ReceiptScreenRoute(onBackClick = onBackClick)
    }
}

fun NavController.navigateToReceipt() {
    this.navigate(RECEIPT_ROUTE)
}
