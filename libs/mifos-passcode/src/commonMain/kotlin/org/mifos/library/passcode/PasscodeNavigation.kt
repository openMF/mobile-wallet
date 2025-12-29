/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.library.passcode

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val PASSCODE_SCREEN = "passcode_screen"

enum class Intention(val value: String) {
    CHANGE_PASSCODE("change_passcode"),
    LOGIN_WITH_PASSCODE("login_with_passcode"),
    CREATE_PASSCODE("create_passcode"),
    ;

    companion object {
        fun fromValue(value: String): Intention =
            Intention.entries.firstOrNull { it.value == value } ?: CREATE_PASSCODE
    }
}

const val INTENTION = "intention"
const val PASSCODE_ROUTE = "$PASSCODE_SCREEN?$INTENTION={$INTENTION}"

fun NavGraphBuilder.passcodeRoute(
    onForgotButton: () -> Unit,
    onSkipButton: () -> Unit,
    onPasscodeFlowComplete: () -> Unit,
) {
    composable(
        route = PASSCODE_ROUTE,
        arguments = listOf(
            navArgument(INTENTION) {
                defaultValue = Intention.LOGIN_WITH_PASSCODE.value
                type = NavType.StringType
            },
        ),
    ) {
        PasscodeScreen(
            onForgotButton = onForgotButton,
            onSkipButton = onSkipButton,
            onPasscodeFlowComplete = onPasscodeFlowComplete,
        )
    }
}

fun NavController.navigateToPasscodeScreen(options: NavOptions? = null, intention: Intention) {
    val route = "$PASSCODE_SCREEN?$INTENTION=${intention.value}"
    navigate(route, options)
}
