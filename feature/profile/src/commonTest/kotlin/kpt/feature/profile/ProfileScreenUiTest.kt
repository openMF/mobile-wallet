/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.profile

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kpt.feature.profile.demo.ProfileDemoBody
import kotlin.test.Test

/**
 * Compose Multiplatform UI test for [ProfileScreen].
 *
 * ProfileScreen is a pure-UI screen with no ViewModel — it renders a static
 * placeholder hero-card. The test renders it directly inside [KptTheme] and
 * asserts the always-present root scaffold node identified by
 * [TestTags.Profile.SCREEN].
 */
@OptIn(ExperimentalTestApi::class)
class ProfileScreenUiTest {

    @Test
    fun screenIsDisplayed() = runComposeUiTest {
        setContent {
            KptTheme {
                // In production cmp-navigation's BackboneRegistry.profileBody supplies this body.
                ProfileScreen(profileBody = { ProfileDemoBody() })
            }
        }
        onNodeWithTag(TestTags.Profile.SCREEN).assertIsDisplayed()
    }
}
