/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.read.qr.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.read.qr.ShowQrScreenRoute

const val READ_QR_ROUTE = "read_qr_route"

fun NavController.navigateToReadQr() = navigate(READ_QR_ROUTE)

fun NavGraphBuilder.readQrScreen(
    onBackClick: () -> Unit,
) {
    composable(route = READ_QR_ROUTE) {
        ShowQrScreenRoute(
            backPress = onBackClick,
        )
    }
}
