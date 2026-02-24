/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.authenticator.biometrics

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PLATFORM_AUTHENTICATOR = "platform_authenticator"

fun NavController.navigateToPlatformAuthenticator(navOptions: NavOptions? = null) =
    navigate(PLATFORM_AUTHENTICATOR, navOptions)

fun NavGraphBuilder.platformAuthenticator(
    onAuthenticationSuccess: () -> Unit,
    onForcedLogOut: () -> Unit,
) {
    composable(route = PLATFORM_AUTHENTICATOR) {
        AuthenticationScreen(
            onAuthenticationSuccess,
            onForcedLogOut,
        )
    }
}
