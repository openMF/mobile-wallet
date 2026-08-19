/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.captiveportal

import androidx.compose.runtime.Composable

/**
 * Returns a stable `() -> Unit` lambda that opens the platform-native captive-portal
 * sign-in flow.
 *
 * Per-platform behaviour:
 *  - **Android** — fires `android.settings.WIFI_SETTINGS` via `startActivity`.
 *  - **iOS / Desktop / Web** — no-op; captive portals are handled at the OS level
 *    and these platforms don't expose a programmatic sign-in trigger.
 */
@Composable
expect fun rememberOpenCaptivePortalSignIn(): () -> Unit
