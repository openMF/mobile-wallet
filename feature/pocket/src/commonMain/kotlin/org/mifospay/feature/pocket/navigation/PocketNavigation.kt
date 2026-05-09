/*
 * Copyright 2024 Mifos Initiative
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
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.pocket.dashboard.PocketDashboardScreen
import org.mifospay.feature.pocket.link.LinkAccountScreen

const val POCKET_DASHBOARD_ROUTE = "pocket_dashboard_route"
const val LINK_ACCOUNT_ROUTE = "pocket_link_account_route"

fun NavController.navigateToPocketDashboard(navOptions: NavOptions? = null) =
    navigate(POCKET_DASHBOARD_ROUTE, navOptions)

fun NavController.navigateToLinkAccount(navOptions: NavOptions? = null) =
    navigate(LINK_ACCOUNT_ROUTE, navOptions)

fun NavGraphBuilder.pocketDashboardScreen(
    navigateBack: () -> Unit,
    navigateToLinkAccount: () -> Unit,
    navigateToAccountDetails: (Long) -> Unit,
) {
    composable(route = POCKET_DASHBOARD_ROUTE) {
        PocketDashboardScreen(
            navigateBack = navigateBack,
            navigateToLinkAccount = navigateToLinkAccount,
            navigateToAccountDetails = navigateToAccountDetails,
        )
    }
}

fun NavGraphBuilder.linkAccountScreen(
    navigateBack: () -> Unit,
    onLinkSuccess: () -> Unit,
) {
    composable(route = LINK_ACCOUNT_ROUTE) {
        LinkAccountScreen(
            navigateBack = navigateBack,
            onLinkSuccess = onLinkSuccess,
        )
    }
}
