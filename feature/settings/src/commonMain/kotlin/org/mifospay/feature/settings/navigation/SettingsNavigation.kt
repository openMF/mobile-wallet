/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import org.mifospay.core.ui.composableWithSlideTransitions
import org.mifospay.feature.settings.SettingsScreenRoute

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_ROUTE, navOptions)
}

fun NavGraphBuilder.settingsScreen(
    onBackPress: () -> Unit,
    onLogout: () -> Unit,
    onChangePasscode: () -> Unit,
    navigateToEditPasswordScreen: () -> Unit,
    navigateToFaqScreen: () -> Unit,
    navigateToNotificationScreen: () -> Unit,
) {
    composableWithSlideTransitions(route = SETTINGS_ROUTE) {
        SettingsScreenRoute(
            backPress = onBackPress,
            onEditPassword = navigateToEditPasswordScreen,
            onLogout = onLogout,
            onChangePasscode = onChangePasscode,
            navigateToFaqScreen = navigateToFaqScreen,
            navigateToNotificationScreen = navigateToNotificationScreen,
        )
    }
}
