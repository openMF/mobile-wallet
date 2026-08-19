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

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import sergio.sastre.composable.preview.scanner.common.CommonComposablePreviewScanner

/**
 * Device-free CMP render tier (SCREENSHOT_TEST.md CMP-PRIMARY). Auto-discovers every commonMain
 * `@Preview` in this feature via `CommonComposablePreviewScanner`, then renders each one off
 * `desktopTest` (JVM — no emulator, no Robolectric) and captures via roborazzi-compose-desktop.
 * Drives `recordRoborazziDesktop` / `verifyRoborazziDesktop`.
 *
 * NB (desktop path): the `roborazzi-compose-preview-scanner-support` bridge (`ComposablePreview
 * .captureRoboImage()`) is Android/Robolectric-only (published `aar`, no JVM variant), so the
 * device-free desktop path renders each discovered preview manually inside its own
 * `runDesktopComposeUiTest` block (fresh composition per preview → no `setContent`-once clash).
 *
 * This is the template's demonstrator; every fork gets the same tier automatically via
 * `CMPFeatureConventionPlugin`, so `kmp-screen-gen`'s emitted previews become CI-renderable
 * with zero per-feature wiring.
 */
@OptIn(ExperimentalTestApi::class)
class SettingsPreviewScreenshotTest {

    @Test
    fun captureAllPreviews() {
        CommonComposablePreviewScanner()
            .scanPackageTrees("kpt.feature.settings")
            .getPreviews()
            .forEachIndexed { index, preview ->
                runDesktopComposeUiTest {
                    setContent { preview() }
                    // Golden under src/desktopTest/resources (committed) so CI `verifyRoborazziDesktop`
                    // has a baseline to compare a fresh render against — not a throwaway build/ dir.
                    onRoot().captureRoboImage(
                        "src/desktopTest/resources/screenshots/settings/preview_$index.png",
                    )
                }
            }
    }
}
