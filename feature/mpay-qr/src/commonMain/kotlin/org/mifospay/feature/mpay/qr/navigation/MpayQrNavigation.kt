/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.mpay.qr.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import org.mifospay.core.ui.composableWithPushTransitions
import org.mifospay.feature.mpay.qr.MpayQrScreen

const val MPAY_QR_ROUTE = "mpay_qr_route"

fun NavGraphBuilder.mpayQrScreen(
    navigateBack: () -> Unit,
    navigateToSendScreen: (String) -> Unit,
    navigateToPayeeDetailsScreen: (String) -> Unit,
) {
    composableWithPushTransitions(
        route = MPAY_QR_ROUTE,
    ) {
        MpayQrScreen(
            navigateBack = navigateBack,
            // from #1906 pr
//            navigateToSendScreen = navigateToSendScreen,
//            navigateToPayeeDetailsScreen = navigateToPayeeDetailsScreen,
        )
    }
}

fun NavController.navigateToMpayQrScreen(navOptions: NavOptions? = null) {
    navigate(MPAY_QR_ROUTE, navOptions)
}
