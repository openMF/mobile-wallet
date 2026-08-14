/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
import androidx.compose.ui.window.ComposeUIViewController
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
