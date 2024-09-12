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
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import androidx.navigation.compose.rememberNavController
import com.mifos.passcode.BiometricUtilAndroidImpl
import com.mifos.passcode.CipherUtilAndroidImpl
import com.mifos.passcode.data.PasscodeRepository
import com.mifos.passcode.data.PasscodeRepositoryImpl
import com.mifos.passcode.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     */
    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private val viewModel: MainActivityViewModel by viewModels()

    private val bioMetricUtil by lazy {
        BiometricUtilAndroidImpl(this, CipherUtilAndroidImpl())
    }

    private lateinit var passcodeRepository: PasscodeRepository

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

        bioMetricUtil.preparePrompt(
            title = getString(R.string.biometric_auth_title),
            subtitle = "",
            description = getString(R.string.biometric_auth_description),
        )
        passcodeRepository = PasscodeRepositoryImpl(PreferenceManager())

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
                        bioMetricUtil = bioMetricUtil,
                        enableBiometric = true,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }
}
