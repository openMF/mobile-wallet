/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.autopay

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions

object AutoPayNavigation {
    const val AUTO_PAY_ROUTE = "autopay"
    const val AUTO_PAY_SETUP_ROUTE = "autopay/setup"
    const val AUTO_PAY_RULES_ROUTE = "autopay/rules"
    const val AUTO_PAY_PREFERENCES_ROUTE = "autopay/preferences"
    const val AUTO_PAY_HISTORY_ROUTE = "autopay/history"
}

fun NavController.navigateToAutoPay(navOptions: NavOptions? = null) {
    navigate(AutoPayNavigation.AUTO_PAY_ROUTE, navOptions)
}

fun NavController.navigateToAutoPaySetup(navOptions: NavOptions? = null) {
    navigate(AutoPayNavigation.AUTO_PAY_SETUP_ROUTE, navOptions)
}

fun NavController.navigateToAutoPayRules(navOptions: NavOptions? = null) {
    navigate(AutoPayNavigation.AUTO_PAY_RULES_ROUTE, navOptions)
}

fun NavController.navigateToAutoPayPreferences(navOptions: NavOptions? = null) {
    navigate(AutoPayNavigation.AUTO_PAY_PREFERENCES_ROUTE, navOptions)
}

fun NavController.navigateToAutoPayHistory(navOptions: NavOptions? = null) {
    navigate(AutoPayNavigation.AUTO_PAY_HISTORY_ROUTE, navOptions)
}

fun NavGraphBuilder.autoPayGraph(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.navigateUp() },
) {
    composable(AutoPayNavigation.AUTO_PAY_ROUTE) {
        AutoPayScreen(
            onNavigateToSetup = {
                navController.navigateToAutoPaySetup()
            },
            onNavigateToRules = {
                navController.navigateToAutoPayRules()
            },
            onNavigateToPreferences = {
                navController.navigateToAutoPayPreferences()
            },
            onNavigateToHistory = {
                navController.navigateToAutoPayHistory()
            },
        )
    }

    composable(AutoPayNavigation.AUTO_PAY_SETUP_ROUTE) {
        AutoPaySetupScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composable(AutoPayNavigation.AUTO_PAY_RULES_ROUTE) {
        AutoPayRulesScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composable(AutoPayNavigation.AUTO_PAY_PREFERENCES_ROUTE) {
        AutoPayPreferencesScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composable(AutoPayNavigation.AUTO_PAY_HISTORY_ROUTE) {
        AutoPayHistoryScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}
