/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.chooseAuthOption

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val CHOOSE_AUTH_OPTION_ROUTE = "choose_authentication_option_route"

fun NavController.navigateToChooseAuthOptionScreen(navOptions: NavOptions? = null) =
    navigate(CHOOSE_AUTH_OPTION_ROUTE, navOptions)

fun NavGraphBuilder.chooseAuthOptionScreen(
    onBiometricsRegistrationSuccess: () -> Unit,
    onChoosePasscode: () -> Unit,
) {
    composable(route = CHOOSE_AUTH_OPTION_ROUTE) {
        ChooseAuthOptionContent(
            onBiometricsRegistrationSuccess = onBiometricsRegistrationSuccess,
            onNavigateToPasscode = onChoosePasscode,
        )
    }
}
