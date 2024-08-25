/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SEARCH_ROUTE = "search_route"

fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
) {
    composable(route = SEARCH_ROUTE) {
        SearchScreenRoute(
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToSearch() = navigate(SEARCH_ROUTE)
