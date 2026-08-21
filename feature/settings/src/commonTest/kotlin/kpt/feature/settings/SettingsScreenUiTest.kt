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
import kotlin.test.Test

/**
 * Compose Multiplatform UI test for [SettingsScreenContent] — the template-owned Settings shell,
 * always rendered regardless of dialog state (RULE-KMP-COMPOSE-UITEST-001 CU-1..CU-3).
 *
 * This fork has no demo dialog/dev-menu seam, so the shell is exercised directly with no-op
 * callbacks; production wires the real callbacks via `cmp-navigation`. Asserts the always-present
 * root scaffold node identified by [TestTags.Settings.SCREEN].
 */
@OptIn(ExperimentalTestApi::class)
class SettingsScreenUiTest {

    @Test
    fun screenIsDisplayed() = runComposeUiTest {
        setContent {
            KptTheme {
                SettingsScreenContent(
                    onBackClick = {},
                    onThemeCardClick = {},
                    onLanguageCardClick = {},
                    onSyncAndDraftsClick = {},
                )
            }
        }
        onNodeWithTag(TestTags.Settings.SCREEN).assertIsDisplayed()
    }
}
