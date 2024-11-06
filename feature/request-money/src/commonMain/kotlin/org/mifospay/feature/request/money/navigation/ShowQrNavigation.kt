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
import androidx.navigation.NavOptions
import org.mifospay.core.ui.composableWithPushTransitions
import org.mifospay.feature.request.money.ShowQrScreen

const val SHOW_QR_ROUTE = "show_qr_route"

fun NavGraphBuilder.showQrScreen(
    navigateBack: () -> Unit,
) {
    composableWithPushTransitions(
        route = SHOW_QR_ROUTE,
    ) {
        ShowQrScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateToShowQrScreen(navOptions: NavOptions? = null) {
    navigate(SHOW_QR_ROUTE, navOptions)
}
