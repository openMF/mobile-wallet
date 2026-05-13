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

const val ROOT_MIFOS_PASSCODE_ROUTE = "root_mifos_passcode_route"
const val RE_AUTH_MIFOS_PASSCODE_ROUTE = "reauth_mifos_passcode_route"

@Serializable
@SerialName(ROOT_MIFOS_PASSCODE_ROUTE)
data object RootPasscodeRoute

@Serializable
@SerialName(RE_AUTH_MIFOS_PASSCODE_ROUTE)
data object ReAuthPasscodeRoute

@Serializable
data class InternalPasscodeRoute(
    val verificationKey: String? = null,
    val allowBiometricAuth: Boolean = true,
)

fun NavController.navigateToRootMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(RootPasscodeRoute, navOptions)

fun NavController.navigateToReAuthMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(ReAuthPasscodeRoute, navOptions)

fun NavController.navigateToInternalMifosPasscodeScreen(
    verificationKey: String? = null,
    allowBiometricAuth: Boolean = true,
    navOptions: NavOptions? = null,
) = navigate(InternalPasscodeRoute(verificationKey, allowBiometricAuth), navOptions)

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.rootMifosPasscodeScreen(
    navigateToLogin: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    onPasscodeCreation: () -> Unit = {},
    onAuthenticationFailed: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
) {
    composableWithStayTransitions<RootPasscodeRoute> {
        MifosPasscode(
            onAuthenticationSuccess = onAuthenticationSuccess,
            navigateToLogin = navigateToLogin,
            onPasscodeCreation = onPasscodeCreation,
            onAuthenticationFailed = onAuthenticationFailed,
            onPasscodeChanged = onPasscodeChanged,
            allowBackNavigation = false,
            allowBiometricAuth = true,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.reAuthMifosPasscodeScreen(
    navigateToLogin: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    onAuthenticationFailed: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
) {
    composableWithSlideTransitions<ReAuthPasscodeRoute> {
        MifosPasscode(
            onAuthenticationSuccess = onAuthenticationSuccess,
            navigateToLogin = navigateToLogin,
            onAuthenticationFailed = onAuthenticationFailed,
            onPasscodeChanged = onPasscodeChanged,
            allowBackNavigation = false,
            allowBiometricAuth = true,
        )
    }
}

/**
 * In-app passcode prompt for sensitive operations (change passcode, disable
 * biometrics, intra-bank transfer auth gate).
 *
 * @param verificationKey Carried via [InternalPasscodeRoute]; forwarded back to
 *        the caller's saved-state-handle on success/failure so the previous
 *        screen can observe the round-trip result. Null if the caller doesn't
 *        need the round-trip channel.
 * @param onAuthenticationSuccess `(verificationKey) -> Unit` — typically writes
 *        `true` to the previous back stack entry's saved-state-handle and pops.
 * @param onAuthenticationFailed `(verificationKey) -> Unit` — typically writes
 *        `false` to the previous saved-state-handle and pops.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.internalMifosPasscodeScreen(
    navigateToLogin: () -> Unit,
    onAuthenticationSuccess: (verificationKey: String?) -> Unit,
    onAuthenticationFailed: (verificationKey: String?) -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onBackPress: () -> Unit = {},
) {
    composableWithSlideTransitions<InternalPasscodeRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<InternalPasscodeRoute>()
        MifosPasscode(
            onAuthenticationSuccess = { onAuthenticationSuccess(route.verificationKey) },
            navigateToLogin = navigateToLogin,
            onPasscodeCreation = {},
            onAuthenticationFailed = { onAuthenticationFailed(route.verificationKey) },
            onPasscodeChanged = onPasscodeChanged,
            onBackPress = onBackPress,
            allowBackNavigation = true,
            allowBiometricAuth = route.allowBiometricAuth,
        )
    }
}
