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

const val ROOT_MIFOS_PASSCODE_ROUTE = "root_mifos_passcode_route"
const val RE_AUTH_MIFOS_PASSCODE_ROUTE = "reauth_mifos_passcode_route"
const val INTERNAL_MIFOS_PASSCODE_ROUTE = "internal_mifos_passcode_route"

fun NavController.navigateToRootMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(ROOT_MIFOS_PASSCODE_ROUTE, navOptions)
fun NavController.navigateToReAuthMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(RE_AUTH_MIFOS_PASSCODE_ROUTE, navOptions)

fun NavController.navigateToInternalMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(INTERNAL_MIFOS_PASSCODE_ROUTE, navOptions)

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.mifosRootPasscodeScreen(
    onForgotButton: () -> Unit,
    onPasscodeConfirm: () -> Unit,
    onPasscodeCreation: () -> Unit,
) {

    composable(route = ROOT_MIFOS_PASSCODE_ROUTE) {
        MifosPasscode(
            onForgotButton,
            onPasscodeConfirm,
            onPasscodeCreation,
        ) {}
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.mifosReAuthPasscodeScreen(
    onForgotButton: () -> Unit,
    onPasscodeConfirm: () -> Unit,
) {
    composable(route = RE_AUTH_MIFOS_PASSCODE_ROUTE) {
        MifosPasscode(
            onForgotButton,
            onPasscodeConfirm,
            { },
        ) {}
    }
}

fun NavGraphBuilder.internalPasscodeVerificationScreen(
    onForgotButton: () -> Unit,
    onPasscodeConfirm: () -> Unit,
    onPasscodeCreation: () -> Unit,
    onPasscodeRejected: () -> Unit,
) {
    composable(route = INTERNAL_MIFOS_PASSCODE_ROUTE) {
        MifosPasscode(
            onForgotButton,
            onPasscodeConfirm,
            onPasscodeCreation,
            onPasscodeRejected,
        )
    }
}
