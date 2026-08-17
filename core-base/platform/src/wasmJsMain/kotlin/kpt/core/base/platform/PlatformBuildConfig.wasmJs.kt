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
 * WasmJS implementation of PlatformBuildConfig.
 *
 * In WasmJS, we cannot access process.env directly like in JS.
 * Default to debug mode for development builds.
 */
actual object PlatformBuildConfig {
    actual val isDebug: Boolean = true
}
