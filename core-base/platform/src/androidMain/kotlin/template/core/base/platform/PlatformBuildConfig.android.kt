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

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android implementation of PlatformBuildConfig.
 * Uses Koin to get the Android context and check if the app is debuggable.
 */
actual object PlatformBuildConfig : KoinComponent {
    private val context: android.content.Context by inject()

    actual val isDebug: Boolean
        get() = (
            context.applicationInfo.flags and
                android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
            ) != 0
}
