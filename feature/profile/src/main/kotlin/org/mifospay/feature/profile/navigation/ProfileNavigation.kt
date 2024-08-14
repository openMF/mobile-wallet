/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.profile.ProfileRoute

const val PROFILE_ROUTE = "profile_route"

fun NavController.navigateToProfile(navOptions: NavOptions) = navigate(PROFILE_ROUTE, navOptions)

fun NavGraphBuilder.profileScreen(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
) {
    composable(route = PROFILE_ROUTE) {
        ProfileRoute(
            onEditProfile = onEditProfile,
            onSettings = onSettings,
        )
    }
}
