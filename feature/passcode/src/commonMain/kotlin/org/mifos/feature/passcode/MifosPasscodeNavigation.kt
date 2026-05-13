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
import androidx.navigation.toRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import template.core.base.ui.composableWithSlideTransitions
import template.core.base.ui.composableWithStayTransitions

// Kept as constants so they can be used as startDestination strings in NavHost
const val ROOT_MIFOS_PASSCODE_ROUTE = "root_mifos_passcode_route"
const val RE_AUTH_MIFOS_PASSCODE_ROUTE = "reauth_mifos_passcode_route"

// @SerialName ensures composable<T> registers at the same route string as the constant above,
// so startDestination = ROOT_MIFOS_PASSCODE_ROUTE in NavHost still matches.
@Serializable
@SerialName(ROOT_MIFOS_PASSCODE_ROUTE)
data object RootPasscodeRoute

@Serializable
@SerialName(RE_AUTH_MIFOS_PASSCODE_ROUTE)
data object ReAuthPasscodeRoute

@Serializable
data class InternalPasscodeRoute(val verificationKey: String? = null)

fun NavController.navigateToRootMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(RootPasscodeRoute, navOptions)

fun NavController.navigateToReAuthMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(ReAuthPasscodeRoute, navOptions)

fun NavController.navigateToInternalMifosPasscodeScreen(
    verificationKey: String? = null,
    navOptions: NavOptions? = null,
) = navigate(InternalPasscodeRoute(verificationKey), navOptions)

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.rootMifosPasscodeScreen(
    onForgotButton: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    onPasscodeCreation: () -> Unit = {},
    onAuthenticationFailed: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onDisableBiometrics: () -> Unit = {},
) {
    composableWithStayTransitions<RootPasscodeRoute> {
        MifosPasscode(
            onForgotButton = onForgotButton,
            onAuthenticationSuccess = onAuthenticationSuccess,
            onPasscodeCreation = onPasscodeCreation,
            onAuthenticationFailed = onAuthenticationFailed,
            onPasscodeChanged = onPasscodeChanged,
            onDisableBiometrics = onDisableBiometrics,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.reAuthMifosPasscodeScreen(
    onForgotButton: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    onAuthenticationFailed: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onDisableBiometrics: () -> Unit = {},
) {
    composableWithSlideTransitions<ReAuthPasscodeRoute> {
        MifosPasscode(
            onForgotButton = onForgotButton,
            onAuthenticationSuccess = onAuthenticationSuccess,
            onPasscodeCreation = {},
            onAuthenticationFailed = onAuthenticationFailed,
            onPasscodeChanged = onPasscodeChanged,
            onDisableBiometrics = onDisableBiometrics,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.internalMifosPasscodeScreen(
    onForgotButton: () -> Unit,
    onAuthenticationSuccess: (String?) -> Unit,
    onAuthenticationFailed: (String?) -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onDisableBiometrics: () -> Unit = {},
) {
    composableWithSlideTransitions<InternalPasscodeRoute> { backStackEntry ->
        val verificationKey = backStackEntry.toRoute<InternalPasscodeRoute>().verificationKey
        MifosPasscode(
            onForgotButton = onForgotButton,
            onAuthenticationSuccess = { onAuthenticationSuccess(verificationKey) },
            onPasscodeCreation = {},
            onAuthenticationFailed = { onAuthenticationFailed(verificationKey) },
            onPasscodeChanged = onPasscodeChanged,
            onDisableBiometrics = onDisableBiometrics,
        )
    }
}
