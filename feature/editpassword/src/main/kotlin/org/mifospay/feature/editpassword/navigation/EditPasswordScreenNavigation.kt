/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.editpassword.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.editpassword.EditPasswordScreen

const val EDIT_PASSWORD_ROUTE = "edit_password_route"

fun NavGraphBuilder.editPasswordScreen(
    onBackPress: () -> Unit,
    onCancelChanges: () -> Unit,
) {
    composable(route = EDIT_PASSWORD_ROUTE) {
        EditPasswordScreen(
            onBackPress = onBackPress,
            onCancelChanges = onCancelChanges,
        )
    }
}

fun NavController.navigateToEditPassword() {
    this.navigate(EDIT_PASSWORD_ROUTE)
}
