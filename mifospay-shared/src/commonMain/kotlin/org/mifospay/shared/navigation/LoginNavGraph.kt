/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import org.mifos.library.passcode.navigateToPasscodeScreen
import org.mifospay.feature.auth.navigation.LOGIN_ROUTE
import org.mifospay.feature.auth.navigation.loginScreen
import org.mifospay.feature.auth.navigation.mobileVerificationScreen
import org.mifospay.feature.auth.navigation.navigateToLogin
import org.mifospay.feature.auth.navigation.navigateToSignup
import org.mifospay.feature.auth.navigation.signupScreen
import org.mifospay.feature.auth.socialSignup.navigateToSignupMethod
import org.mifospay.feature.auth.socialSignup.signupMethodScreen

internal fun NavGraphBuilder.loginNavGraph(navController: NavController) {
    navigation(
        route = MifosNavGraph.LOGIN_GRAPH,
        startDestination = LOGIN_ROUTE,
    ) {
        loginScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToPasscodeScreen = navController::navigateToPasscodeScreen,
            onNavigateToSignupScreen = navController::navigateToSignupMethod,
        )

        signupMethodScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToSignUp = {
                navController.navigateToSignup(savingsProductId = it)
            },
        )

        signupScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToLogin = navController::navigateToLogin,
        )

        mobileVerificationScreen(
            onNavigateBack = navController::popBackStack,
            onOtpVerificationSuccess = { fullNumber ->
                navController.navigateToSignup(mobileNumber = fullNumber)
            },
        )
    }
}
