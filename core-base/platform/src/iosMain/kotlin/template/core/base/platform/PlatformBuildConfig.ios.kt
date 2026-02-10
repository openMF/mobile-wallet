/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package template.core.base.platform

import kotlin.experimental.ExperimentalNativeApi

/**
 * iOS implementation of PlatformBuildConfig.
 *
 * Uses Kotlin/Native's Platform.isDebugBinary to detect if the app
 * was compiled in debug mode.
 */
actual object PlatformBuildConfig {
    @OptIn(ExperimentalNativeApi::class)
    actual val isDebug: Boolean = Platform.isDebugBinary
}
