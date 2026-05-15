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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifos.feature.passcode.biometricSetupScreen
import org.mifos.feature.passcode.navigateToBiometricSetupScreen
import org.mifos.feature.passcode.reAuthMifosPasscodeScreen
import org.mifos.feature.passcode.rootMifosPasscodeScreen
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import org.mifospay.feature.onboarding.language.navigation.ONBOARDING_LANGUAGE_ROUTE
import org.mifospay.feature.onboarding.language.navigation.onboardingLanguageScreen
import org.mifospay.shared.instance.InstanceSelectorScreen
import org.mifospay.shared.ui.MifosApp

@Composable
internal fun RootNavGraph(
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    navHostController: NavHostController,
    startDestination: String,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showInstanceSelector by remember { mutableStateOf(false) }

    val systemAuthProvider = platformAuthenticationProvider.current
    val authenticatorStatus by systemAuthProvider.authenticatorStatus.collectAsStateWithLifecycle()

    val isBiometricsAvailable =
        authenticatorStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET)

    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = MifosNavGraph.ROOT_GRAPH,
        modifier = modifier,
    ) {
        loginNavGraph(
            navController = navHostController,
            onShowInstanceSelector = { showInstanceSelector = true },
        )

        onboardingLanguageScreen(
            onNavigateToNext = {
                navHostController.navigate(MifosNavGraph.LOGIN_GRAPH) {
                    popUpTo(ONBOARDING_LANGUAGE_ROUTE) {
                        inclusive = true
                    }
                }
            },
        )

        rootMifosPasscodeScreen(
            onForgotButton = onClickLogout,
            onAuthenticationSuccess = {
                navHostController.popBackStack()
                navHostController.navigateToMainGraph()
            },
            onPasscodeCreation = {
                navHostController.popBackStack()
                if (isBiometricsAvailable) {
                    navHostController.navigateToBiometricSetupScreen()
                } else {
                    navHostController.navigateToMainGraph()
                }
            },
        )

        biometricSetupScreen(
            onBiometricsRegistrationSuccess = {
                navHostController.popBackStack()
                navHostController.navigateToMainGraph()
            },
            onSkipBiometricSetup = {
                navHostController.popBackStack()
                navHostController.navigateToMainGraph()
            },
        )

        reAuthMifosPasscodeScreen(
            onForgotButton = {
                navHostController.popBackStack()
                onClickLogout()
            },
            onAuthenticationSuccess = { navHostController.popBackStack() },
        )

        composable(MifosNavGraph.MAIN_GRAPH) {
            MifosApp(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
                onClickLogout = onClickLogout,
            )
        }
    }

    if (showInstanceSelector) {
        InstanceSelectorScreen(
            onDismiss = { showInstanceSelector = false },
        )
    }
}
