/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
import androidx.compose.ui.window.ComposeUIViewController
// Phase 2 T4 entry-point switch: iOS root ViewController now launches the
// template-shape `cmp.shared.SharedApp` (which composes
// `cmp.navigation.ComposeApp` via the re-homed shell) instead of the legacy
// `org.mifospay.shared.MifosPaySharedApp`. Koin is initialized via
// `cmp.shared.utils.initKoin` which loads `cmp.navigation.di.KoinModules.allModules`
// (fork surface wired in via Phase 2 T6/T7 — see
// `cmp-navigation/src/.../di/KoinModules.kt`).
import cmp.shared.SharedApp
import cmp.shared.utils.initKoin
import platform.Foundation.NSUserDefaults

@Suppress("ktlint:standard:function-naming")
fun MifosViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    },
) {
    SharedApp(
        // iOS shell has no screen-capture / activity-recreate / dark-mode-broadcast
        // primitives to wire — supply no-ops. Locale handling stays as before
        // (writes AppleLanguages user-defaults + synchronize).
        updateScreenCapture = {},
        handleRecreate = {},
        handleThemeMode = {},
        handleAppLocale = { languageTag ->
            if (languageTag != null) {
                // Set specific language
                NSUserDefaults.standardUserDefaults.setObject(
                    listOf(languageTag),
                    forKey = "AppleLanguages",
                )
            } else {
                // System Default: remove app-specific language setting
                NSUserDefaults.standardUserDefaults.removeObjectForKey("AppleLanguages")
            }
            NSUserDefaults.standardUserDefaults.synchronize()
        },
        onSplashScreenRemoved = {},
    )
}
