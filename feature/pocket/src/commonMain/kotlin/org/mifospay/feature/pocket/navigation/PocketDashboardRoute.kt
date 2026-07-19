/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.pocket.screens.PocketDashboardScreen

const val POCKET_DASHBOARD_ROUTE = "pocket_dashboard_route"

fun NavController.navigateToPocketDashboard() {
    navigate(POCKET_DASHBOARD_ROUTE)
}

fun NavGraphBuilder.pocketDashboardScreen(
    navigateBack: () -> Unit,
    navigateToManagePocket: () -> Unit,
    navigateToLoanAccountDetail: (Long) -> Unit,
    navigateToShareAccountDetail: (Long) -> Unit,
    navigateToSavingsAccountDetail: (Long) -> Unit,
) {
    composable(route = POCKET_DASHBOARD_ROUTE) {
        PocketDashboardScreen(
            navigateBack = navigateBack,
            navigateToManagePocket = navigateToManagePocket,
            navigateToLoanAccountDetail = navigateToLoanAccountDetail,
            navigateToShareAccountDetail = navigateToShareAccountDetail,
            navigateToSavingsAccountDetail = navigateToSavingsAccountDetail,
        )
    }
}
