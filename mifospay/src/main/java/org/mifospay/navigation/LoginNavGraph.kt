/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import org.mifos.library.passcode.navigateToPasscodeScreen
import org.mifospay.core.common.Constants
import org.mifospay.feature.auth.navigation.LOGIN_ROUTE
import org.mifospay.feature.auth.navigation.loginScreen
import org.mifospay.feature.auth.navigation.mobileVerificationScreen
import org.mifospay.feature.auth.navigation.navigateToSignup
import org.mifospay.feature.auth.navigation.signupScreen

internal fun NavGraphBuilder.loginNavGraph(navController: NavController) {
    navigation(
        route = MifosNavGraph.LOGIN_GRAPH,
        startDestination = LOGIN_ROUTE,
    ) {
        loginScreen(
            onNavigateToPasscodeScreen = navController::navigateToPasscodeScreen,
            onNavigateToSignupScreen = navController::navigateToSignup,
        )

        signupScreen(
            onLoginSuccess = navController::navigateToPasscodeScreen,
            onRegisterSuccess = navController::navigateToPasscodeScreen,
        )

        mobileVerificationScreen(
            onOtpVerificationSuccess = { fullNumber, extraData ->
                navController.navigateToSignup(
                    savingProductId = extraData[Constants.MIFOS_SAVINGS_PRODUCT_ID] as Int,
                    mobileNumber = fullNumber,
                    country = "Canada",
                    email = extraData[Constants.GOOGLE_EMAIL] as? String ?: "",
                    firstName = extraData[Constants.GOOGLE_GIVEN_NAME] as? String ?: "",
                    lastName = extraData[Constants.GOOGLE_FAMILY_NAME] as? String ?: "",
                    businessName = extraData[Constants.GOOGLE_DISPLAY_NAME] as? String ?: "",
                )
            },
        )
    }
}
