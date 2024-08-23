/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.auth.login.LoginScreen

const val LOGIN_ROUTE = "login_route"

fun NavGraphBuilder.loginScreen(
    onNavigateToPasscodeScreen: () -> Unit,
) {
    composable(route = LOGIN_ROUTE) {
        LoginScreen(navigateToPasscodeScreen = onNavigateToPasscodeScreen)
    }
}

fun NavController.navigateToLogin() {
    this.navigate(LOGIN_ROUTE)
}
