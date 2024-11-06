/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.invoices.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.invoices.InvoiceDetailScreen

const val INVOICE_ROUTE = "invoice_route"
const val INVOICE_DATA_ARG = "invoiceData"

fun NavController.navigateToInvoiceDetail(invoiceData: String) {
    this.navigate("$INVOICE_ROUTE/${Uri.encode(invoiceData)}")
}

fun NavGraphBuilder.invoiceDetailScreen(
    onBackPress: () -> Unit,
) {
    composable(
        route = "$INVOICE_ROUTE/{$INVOICE_DATA_ARG}",
        arguments = listOf(
            navArgument(INVOICE_DATA_ARG) { type = NavType.StringType },
        ),
    ) {
        InvoiceDetailScreen(
            onBackPress = onBackPress,
        )
    }
}
