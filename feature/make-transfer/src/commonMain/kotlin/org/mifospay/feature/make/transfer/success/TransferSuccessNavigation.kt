/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.success

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import org.mifospay.core.ui.composableWithSlideTransitions

private const val TRANSFER_SUCCESS_ROUTE = "transfer_success_route"

fun NavGraphBuilder.transferSuccessScreen(
    navigateBack: () -> Unit,
) {
    composableWithSlideTransitions(route = TRANSFER_SUCCESS_ROUTE) {
        TransferSuccessScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateTransferSuccess(
    navOptions: NavOptions? = null,
) {
    navigate(TRANSFER_SUCCESS_ROUTE, navOptions)
}
