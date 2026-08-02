/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
// Phase 2 T4 entry-point switch: JVM main now launches the template-shape
// `cmp.shared.SharedApp` (which composes `cmp.navigation.ComposeApp` via the
// re-homed shell) instead of the legacy `org.mifospay.shared.MifosPaySharedApp`.
// Koin is initialized via `cmp.shared.utils.initKoin` which loads
// `cmp.navigation.di.KoinModules.allModules` (fork surface wired in via
// Phase 2 T6/T7 — see `cmp-navigation/src/.../di/KoinModules.kt`).
import cmp.shared.SharedApp
import cmp.shared.utils.initKoin
import java.util.Locale

fun main() {
    application {
        initKoin()
        var localeVersion by remember { mutableStateOf(0) }
        val windowState = rememberWindowState()
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "MifosWallet",
        ) {
            // Use key() to force complete recomposition when locale changes.
            key(localeVersion) {
                SharedApp(
                    updateScreenCapture = {},
                    handleRecreate = {
                        // Increment version to trigger recomposition.
                        localeVersion++
                    },
                    handleThemeMode = {},
                    handleAppLocale = { languageTag ->
                        if (languageTag != null) {
                            // Parse language tag and set as default locale
                            val locale = when {
                                languageTag.contains("-") -> {
                                    val parts = languageTag.split("-")
                                    Locale(parts[0], parts[1])
                                }
                                else -> Locale(languageTag)
                            }
                            Locale.setDefault(locale)
                        } else {
                            // System Default: reset to system locale
                            val systemLocale = Locale.getDefault(Locale.Category.DISPLAY)
                            Locale.setDefault(systemLocale)
                        }
                        // Trigger recomposition with new locale
                        localeVersion++
                    },
                    onSplashScreenRemoved = {},
                )
            }
        }
    }
}
