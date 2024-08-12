/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val NEW_SI_ROUTE = "new_si_route"
const val SI_DETAILS_ROUTE = "si_details_route"

fun NavController.navigateToNewSiScreen(
    navOptions: NavOptions? = null,
) = navigate(NEW_SI_ROUTE, navOptions)

fun NavController.navigateToSiDetailsScreen(
    standingInstructionId: String,
    navOptions: NavOptions? = null,
) {
    navigate("$SI_DETAILS_ROUTE/$standingInstructionId", navOptions)
}

fun NavGraphBuilder.newSiScreen(
    onBackClick: () -> Unit,
) {
    composable(route = NEW_SI_ROUTE) {
        NewSIScreenRoute(
            onBackPress = onBackClick,
        )
    }
}

fun NavGraphBuilder.siDetailsScreen(
    onClickCreateNew: () -> Unit,
    onBackPress: () -> Unit,
) {
    composable(
        route = "$SI_DETAILS_ROUTE/{standingInstructionId}",
        arguments = listOf(navArgument("standingInstructionId") { type = NavType.LongType }),
    ) {
        SIDetailsScreen(
            onClickCreateNew = onClickCreateNew,
            onBackPress = onBackPress,
        )
    }
}
