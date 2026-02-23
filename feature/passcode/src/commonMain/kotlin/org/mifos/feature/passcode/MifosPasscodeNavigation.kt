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

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val ROOT_MIFOS_PASSCODE_ROUTE = "root_mifos_passcode_route"
const val INTERNAL_MIFOS_PASSCODE_ROUTE = "internal_mifos_passcode_route"

fun NavController.navigateToRootMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(ROOT_MIFOS_PASSCODE_ROUTE, navOptions)

fun NavController.navigateToInternalMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(INTERNAL_MIFOS_PASSCODE_ROUTE, navOptions)

fun NavGraphBuilder.mifosRootPasscodeScreen(
    onForgotButton: () -> Unit,
    onSkipButton: () -> Unit,
    onPasscodeConfirm: () -> Unit,
    onPasscodeCreation: () -> Unit,
    onPasscodeRejected: () -> Unit,
) {
    composable(route = ROOT_MIFOS_PASSCODE_ROUTE) {
        MifosPasscode(
            onForgotButton,
            onSkipButton,
            onPasscodeConfirm,
            onPasscodeCreation,
            onPasscodeRejected,
        )
    }
}
fun NavGraphBuilder.internalPasscodeVerificationScreen(
    onForgotButton: () -> Unit,
    onPasscodeConfirm: () -> Unit,
    onPasscodeRejected: () -> Unit,
    onPasscodeCreation: () -> Unit = {},
) {
    composable(route = INTERNAL_MIFOS_PASSCODE_ROUTE) {
        MifosPasscode(
            onForgotButton,
            onSkipButton = {},
            onPasscodeConfirm,
            onPasscodeCreation = onPasscodeCreation,
            onPasscodeRejected,
        )
    }
}
