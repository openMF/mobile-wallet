/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.kyc.KYCLevel2Screen

const val KYC_LEVEL_2_ROUTE = "kyc_level_2_route"

fun NavController.navigateToKYCLevel2() {
    this.navigate(KYC_LEVEL_2_ROUTE)
}

fun NavGraphBuilder.kycLevel2Screen(
    onSuccessKyc2: () -> Unit,
) {
    composable(route = KYC_LEVEL_2_ROUTE) {
        KYCLevel2Screen(
            onSuccessKyc2 = onSuccessKyc2,
        )
    }
}
