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

/**
 * JS implementation of PlatformBuildConfig.
 *
 * Checks if running in development mode by examining NODE_ENV environment variable.
 * Defaults to true (debug mode) if unable to determine.
 */
actual object PlatformBuildConfig {
    actual val isDebug: Boolean = checkIsDebug()

    private fun checkIsDebug(): Boolean {
        return try {
            js("process.env.NODE_ENV !== 'production'") as Boolean
        } catch (_: dynamic) {
            // In browser environment or when process is not available, default to debug
            true
        }
    }
}
