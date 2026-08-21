/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings

import androidx.compose.runtime.Composable
import kpt.core.designsystem.theme.KptTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/*
 * Reference @Preview for the device-free CMP render tier (SCREENSHOT_TEST.md CMP-PRIMARY).
 * `CommonComposablePreviewScanner` auto-discovers this from commonMain and renders it off
 * `desktopTest` via `verifyRoborazziDesktop` — no emulator, no Robolectric. Every fork's
 * `kmp-screen-gen` emits one of these per screen state; this is the template's demonstrator.
 */
@Preview
@Composable
internal fun SettingsScreenContentPreview() {
    KptTheme {
        SettingsScreenContent(
            onBackClick = {},
            onThemeCardClick = {},
            onLanguageCardClick = {},
            onSyncAndDraftsClick = {},
        )
    }
}
