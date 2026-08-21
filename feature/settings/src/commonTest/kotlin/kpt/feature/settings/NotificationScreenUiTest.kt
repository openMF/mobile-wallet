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
 * Compose Multiplatform UI test for [NotificationScreen].
 *
 * NotificationScreen is a pure-UI screen that renders a static hero-card
 * placeholder with no ViewModel or async state. The test renders it directly
 * inside [KptTheme] with a no-op back-click callback and asserts the always-
 * present root scaffold node identified by [TestTags.Notification.SCREEN].
 */
@OptIn(ExperimentalTestApi::class)
class NotificationScreenUiTest {

    @Test
    fun screenIsDisplayed() = runComposeUiTest {
        setContent {
            KptTheme {
                NotificationScreen(
                    onBackClick = {},
                )
            }
        }
        onNodeWithTag(TestTags.Notification.SCREEN).assertIsDisplayed()
    }
}
