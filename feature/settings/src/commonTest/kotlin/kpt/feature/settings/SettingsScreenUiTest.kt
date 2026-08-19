/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kpt.feature.settings.demo.SettingsDemoBody
import kotlin.test.Test

/**
 * Compose Multiplatform UI test for the settings inner body [SettingsDemoBody] (the fork-owned seam
 * rendered by `cmp-navigation`'s `BackboneRegistry.settingsBody` inside the template `SettingsScreen`
 * shell).
 *
 * It is a pure-UI body — all dialog state is managed internally; no ViewModel is needed. The test
 * renders it directly inside [KptTheme] with no-op callbacks and asserts the always-present root
 * scaffold node identified by [TestTags.Settings.SCREEN].
 */
@OptIn(ExperimentalTestApi::class)
class SettingsScreenUiTest {

    @Test
    fun screenIsDisplayed() = runComposeUiTest {
        setContent {
            KptTheme {
                // In production cmp-navigation's BackboneRegistry.settingsBody supplies this body.
                SettingsDemoBody(
                    onBackClick = {},
                    onSyncAndDraftsClick = {},
                )
            }
        }
        onNodeWithTag(TestTags.Settings.SCREEN).assertIsDisplayed()
    }
}
