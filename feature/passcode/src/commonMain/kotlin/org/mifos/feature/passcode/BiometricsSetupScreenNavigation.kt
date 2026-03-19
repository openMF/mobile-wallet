/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.feature.passcode

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val BIOMETRIC_SETUP_ROUTE = "biometrics_setup_route"

fun NavController.navigateToBiometricSetupScreen(navOptions: NavOptions? = null) =
    navigate(BIOMETRIC_SETUP_ROUTE, navOptions)

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.biometricSetupScreen(
    onBiometricsRegistrationSuccess: () -> Unit,
    onSkipBiometricSetup: () -> Unit,
) {
    composable(route = BIOMETRIC_SETUP_ROUTE) {
        BiometricSetupScreen(
            onBiometricsRegistrationSuccess,
            onSkipBiometricSetup,
        )
    }
}
