/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
@file:Suppress("MaxLineLength")

package org.mifospay.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.auth.signup.SignupScreen

const val SIGNUP_ROUTE = "signup_route"

fun NavGraphBuilder.signupScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: (String) -> Unit,
) {
    composable(
        route = "$SIGNUP_ROUTE?savingsProductId={savingsProductId}" +
            "&mobileNumber={mobileNumber}&businessName={businessName}",
        arguments = listOf(
            navArgument("savingsProductId") {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument("mobileNumber") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("businessName") {
                type = NavType.StringType
                defaultValue = ""
            },
        ),
    ) {
        SignupScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}

fun NavController.navigateToSignup(
    savingsProductId: Int = 0,
    mobileNumber: String = "",
    businessName: String = "",
) {
    this.navigate(
        "$SIGNUP_ROUTE?savingsProductId=$savingsProductId" +
            "&mobileNumber=$mobileNumber&businessName=$businessName",
    )
}
