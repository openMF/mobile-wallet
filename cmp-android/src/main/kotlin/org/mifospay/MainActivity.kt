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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import org.mifospay.core.ui.utils.ShareUtils
import org.mifospay.shared.MainUiState
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.MifosPayViewModel

class MainActivity : AppCompatActivity() {
    private val networkMonitor: NetworkMonitor by inject()
    private val timeZoneMonitor: TimeZoneMonitor by inject()
    private val viewModel: MifosPayViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Initialize FileKit
        FileKit.init(this)

        var uiState: MainUiState by mutableStateOf(MainUiState.Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { state ->
                        uiState = state
                        if (state is MainUiState.Success) {
                            val languageTag = state.language.localName
                            val currentAppLocales = AppCompatDelegate.getApplicationLocales()

                            val isRequestedDefault = languageTag.isNullOrBlank()
                            val isCurrentDefault = currentAppLocales.isEmpty

                            val shouldUpdate = if (isRequestedDefault) {
                                !isCurrentDefault
                            } else {
                                languageTag != currentAppLocales.toLanguageTags()
                            }

                            if (shouldUpdate) {
                                val appLocale: LocaleListCompat = if (isRequestedDefault) {
                                    LocaleListCompat.getEmptyLocaleList()
                                } else {
                                    LocaleListCompat.forLanguageTags(languageTag)
                                }
                                AppCompatDelegate.setApplicationLocales(appLocale)
                            }
                        }
                    }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainUiState.Loading -> true
                is MainUiState.Success -> false
            }
        }

        ShareUtils.setActivityProvider { return@setActivityProvider this }

        setContent {
            MifosPaySharedApp(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
            )
        }
    }
}
