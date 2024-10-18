/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.home.HomeScreen

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    onNavigateBack: () -> Unit,
    onRequest: (String) -> Unit,
    onPay: () -> Unit,
    navigateToTransactionDetail: (Long, Long) -> Unit,
    navigateToAccountDetail: (Long) -> Unit,
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(
            onRequest = onRequest,
            onPay = onPay,
            onNavigateBack = onNavigateBack,
            navigateToTransactionDetail = navigateToTransactionDetail,
            navigateToAccountDetail = navigateToAccountDetail,
        )
    }
}
