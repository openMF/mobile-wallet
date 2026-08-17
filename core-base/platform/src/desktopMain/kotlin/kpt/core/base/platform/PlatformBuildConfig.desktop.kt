/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-pay/blob/master/LICENSE.md
 */
package kpt.core.base.platform

/**
 * Desktop (JVM) implementation of PlatformBuildConfig.
 *
 * Uses JVM assertions to detect debug mode. Assertions are typically enabled
 * during development (-ea flag) and disabled in production builds.
 */
actual object PlatformBuildConfig {
    actual val isDebug: Boolean = checkAssertionsEnabled()

    private fun checkAssertionsEnabled(): Boolean {
        var assertionsEnabled = false
        // This assignment will only execute if assertions are enabled
        @Suppress("KotlinConstantConditions")
        assert(true.also { assertionsEnabled = true })
        return assertionsEnabled
    }
}
