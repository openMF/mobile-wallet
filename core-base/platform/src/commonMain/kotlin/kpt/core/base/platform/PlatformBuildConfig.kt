/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package kpt.core.base.platform

/**
 * Provides build configuration information across all platforms.
 * Named PlatformBuildConfig to avoid conflicts with Android's generated BuildConfig.
 */
expect object PlatformBuildConfig {
    /**
     * Returns true if the app is running in debug mode.
     */
    val isDebug: Boolean
}
