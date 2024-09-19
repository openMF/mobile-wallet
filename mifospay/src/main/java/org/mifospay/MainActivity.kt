/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.mifospay.MainActivityUiState.Loading
import org.mifospay.MainActivityUiState.Success
import org.mifospay.core.analytics.AnalyticsHelper
import org.mifospay.core.analytics.LocalAnalyticsHelper
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.LocalTimeZone
import org.mifospay.navigation.MifosNavGraph.LOGIN_GRAPH
import org.mifospay.navigation.MifosNavGraph.PASSCODE_GRAPH
import org.mifospay.navigation.RootNavGraph
import org.mifospay.ui.rememberMifosAppState

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     */


    private val networkMonitor : NetworkMonitor by inject()

    private val timeZoneMonitor : TimeZoneMonitor by inject()

    private val analyticsHelper: AnalyticsHelper by inject()


    private val viewModel: MainActivityViewModel by viewModel()

    private val myWindow: Window by inject { parametersOf(this) }

    private val lazyStats : JankStats  by inject { parametersOf(myWindow) }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                Loading -> true
                is Success -> false
            }
        }

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            val appState = rememberMifosAppState(
                windowSizeClass = calculateWindowSizeClass(this),
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            val navDestination = when (uiState) {
                is Success -> if ((uiState as Success).userData.isAuthenticated) {
                    PASSCODE_GRAPH
                } else {
                    LOGIN_GRAPH
                }

                else -> LOGIN_GRAPH
            }

            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
                LocalTimeZone provides currentTimeZone,
            ) {
                MifosTheme {
                    RootNavGraph(
                        appState = appState,
                        navHostController = navController,
                        startDestination = navDestination,
                        onClickLogout = {
                            viewModel.logOut()
                            navController.navigate(LOGIN_GRAPH) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.isTrackingEnabled = false
    }
}
