/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-pay/blob/master/LICENSE.md
 */

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.di.initKoin
import java.util.Locale

fun main() {
    application {
        initKoin()
        var localeVersion by remember { mutableStateOf(0) }
        // Initialize FileKit
        FileKit.init(appId = "org.mifospay")
        val windowState = rememberWindowState()
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "MifosWallet",
        ) {
            MifosPaySharedApp(
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
            )
        }
    }
}
