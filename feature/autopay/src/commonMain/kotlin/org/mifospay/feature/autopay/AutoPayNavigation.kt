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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import org.mifospay.core.ui.composableWithSlideTransitions

object AutoPayNavigation {
    const val AUTO_PAY_ROUTE = "autopay_route"
    const val AUTO_PAY_SETUP_ROUTE = "autopay_setup_route"
    const val AUTO_PAY_RULES_ROUTE = "autopay_rules_route"
    const val AUTO_PAY_PREFERENCES_ROUTE = "autopay_preferences_route"
    const val AUTO_PAY_HISTORY_ROUTE = "autopay_history_route"
    const val AUTO_PAY_SCHEDULE_DETAILS_ROUTE = "autopay_schedule_details_route"
    const val AUTO_PAY_ADD_BILLER_ROUTE = "autopay_add_biller_route"
    const val AUTO_PAY_BILLER_LIST_ROUTE = "autopay_biller_list_route"
    const val AUTO_PAY_EDIT_BILLER_ROUTE = "autopay_edit_biller_route"
    const val SCHEDULE_ID_ARG = "scheduleId"
    const val BILLER_ID_ARG = "billerId"
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

fun NavController.navigateToAutoPayScheduleDetails(scheduleId: String, navOptions: NavOptions? = null) {
    val route = "${AutoPayNavigation.AUTO_PAY_SCHEDULE_DETAILS_ROUTE}?${AutoPayNavigation.SCHEDULE_ID_ARG}=$scheduleId"
    navigate(route, navOptions)
}

fun NavController.navigateToAddBiller(navOptions: NavOptions? = null) {
    navigate(AutoPayNavigation.AUTO_PAY_ADD_BILLER_ROUTE, navOptions)
}

fun NavController.navigateToBillerList(navOptions: NavOptions? = null) {
    navigate(AutoPayNavigation.AUTO_PAY_BILLER_LIST_ROUTE, navOptions)
}

fun NavController.navigateToEditBiller(billerId: String, navOptions: NavOptions? = null) {
    val route = "${AutoPayNavigation.AUTO_PAY_EDIT_BILLER_ROUTE}?${AutoPayNavigation.BILLER_ID_ARG}=$billerId"
    navigate(route, navOptions)
}

fun NavGraphBuilder.autoPayGraph(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.navigateUp() },
) {
    composableWithSlideTransitions(AutoPayNavigation.AUTO_PAY_ROUTE) {
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
            onNavigateToScheduleDetails = { scheduleId ->
                navController.navigateToAutoPayScheduleDetails(scheduleId)
            },
            onNavigateToAddBiller = {
                navController.navigateToAddBiller()
            },
            onNavigateToBillerList = {
                navController.navigateToBillerList()
            },
            onNavigateBack = onNavigateBack,
            showTopBar = true,
        )
    }

    composableWithSlideTransitions(AutoPayNavigation.AUTO_PAY_SETUP_ROUTE) {
        AutoPaySetupScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composableWithSlideTransitions(AutoPayNavigation.AUTO_PAY_RULES_ROUTE) {
        AutoPayRulesScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composableWithSlideTransitions(AutoPayNavigation.AUTO_PAY_PREFERENCES_ROUTE) {
        AutoPayPreferencesScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composableWithSlideTransitions(AutoPayNavigation.AUTO_PAY_HISTORY_ROUTE) {
        AutoPayHistoryScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composableWithSlideTransitions(
        route = "${AutoPayNavigation.AUTO_PAY_SCHEDULE_DETAILS_ROUTE}?${AutoPayNavigation.SCHEDULE_ID_ARG}={${AutoPayNavigation.SCHEDULE_ID_ARG}}",
        arguments = listOf(
            navArgument(AutoPayNavigation.SCHEDULE_ID_ARG) {
                type = NavType.StringType
                nullable = false
            },
        ),
    ) {
        AutoPayScheduleDetailsScreen(
            onNavigateBack = onNavigateBack,
        )
    }

    composableWithSlideTransitions(AutoPayNavigation.AUTO_PAY_ADD_BILLER_ROUTE) {
        AddBillerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToBillerList = {
                navController.navigateToBillerList(
                    navOptions = navOptions {
                        popUpTo(AutoPayNavigation.AUTO_PAY_ADD_BILLER_ROUTE) {
                            inclusive = true
                        }
                    },
                )
            },
        )
    }

    composableWithSlideTransitions(AutoPayNavigation.AUTO_PAY_BILLER_LIST_ROUTE) {
        BillerListScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToAddBiller = {
                navController.navigateToAddBiller()
            },
            onNavigateToEditBiller = { billerId ->
                navController.navigateToEditBiller(billerId)
            },
        )
    }

    composableWithSlideTransitions(
        route = "${AutoPayNavigation.AUTO_PAY_EDIT_BILLER_ROUTE}?${AutoPayNavigation.BILLER_ID_ARG}={${AutoPayNavigation.BILLER_ID_ARG}}",
        arguments = listOf(
            navArgument(AutoPayNavigation.BILLER_ID_ARG) {
                type = NavType.StringType
                nullable = false
            },
        ),
    ) {
        EditBillerScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}
