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

import android.content.Intent
import android.content.res.Resources
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
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.MifosPayViewModel
import org.mifospay.shared.UserState
import java.util.Locale

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

        var uiState: UserState by mutableStateOf(UserState.UnAuthenticated)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                UserState.UnAuthenticated -> true
                is UserState.Authenticated -> false
            }
        }

        ShareUtils.setActivityProvider { return@setActivityProvider this }

        setContent {
            MifosPaySharedApp(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
                handleAppLocale = { localeTag ->
                    val currentLocales = AppCompatDelegate.getApplicationLocales()
                    val newLocales = if (localeTag != null) {
                        LocaleListCompat.forLanguageTags(localeTag)
                    } else {
                        // System Default: clear app-specific locale
                        LocaleListCompat.getEmptyLocaleList()
                    }

                    // Only update if the locale has actually changed
                    if (currentLocales != newLocales) {
                        AppCompatDelegate.setApplicationLocales(newLocales)
                        // Update Locale.setDefault for non-UI formatting
                        if (localeTag != null) {
                            // Use forLanguageTag to properly parse locales like "en-GB", "pt-BR"
                            Locale.setDefault(Locale.forLanguageTag(localeTag))
                        } else {
                            // Reset to true system default locale from device configuration
                            // Use Resources.getSystem() to get device locale unaffected by app overrides
                            val systemLocale = Resources.getSystem().configuration.locales[0]
                            Locale.setDefault(systemLocale)
                        }
                    }
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

}
