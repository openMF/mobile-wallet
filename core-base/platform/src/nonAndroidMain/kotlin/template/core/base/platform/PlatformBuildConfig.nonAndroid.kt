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
 * Non-Android implementation of PlatformBuildConfig.
 *
 * For Desktop/iOS/Web platforms, we check if assertions are enabled to determine debug mode.
 * In release builds, assertions are typically disabled for performance.
 */
actual object PlatformBuildConfig {
    actual val isDebug: Boolean = checkAssertionsEnabled()

    private fun checkAssertionsEnabled(): Boolean {
        var assertionsEnabled = false
        // This assignment will only happen if assertions are enabled
        @Suppress("KotlinConstantConditions", "AssertionInFunctionCall")
        assert(true.also { assertionsEnabled = true })
        return assertionsEnabled
    }
}
