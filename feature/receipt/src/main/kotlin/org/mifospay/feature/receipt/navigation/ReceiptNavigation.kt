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

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.receipt.ReceiptScreenRoute

const val RECEIPT_ROUTE = "receipt_route"

fun NavGraphBuilder.receiptScreen(
    openPassCodeActivity: (Uri) -> Unit,
    onBackClick: () -> Unit,
) {
    composable(
        route = "$RECEIPT_ROUTE?uri={uri}",
        arguments = listOf(
            navArgument("uri") { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        val uriString = backStackEntry.arguments?.getString("uri")
        val uri = if (uriString != null) Uri.parse(uriString) else null

        ReceiptScreenRoute(
            openPassCodeActivity = { uri?.let { openPassCodeActivity(it) } },
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToReceipt(uri: Uri) {
    this.navigate("$RECEIPT_ROUTE?uri=${Uri.encode(uri.toString())}")
}
