/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.request.money.ShowQrScreenRoute

const val SHOW_QR_ROUTE = "show_qr_route"

fun NavGraphBuilder.showQrScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = "$SHOW_QR_ROUTE/{vpa}",
        arguments = listOf(navArgument("vpa") { type = NavType.StringType }),
    ) {
        ShowQrScreenRoute(backPress = onBackClick)
    }
}

fun NavController.navigateToShowQrScreen(vpa: String) {
    navigate("$SHOW_QR_ROUTE/{$vpa}")
}
