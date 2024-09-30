/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.TimeZone
import org.mifospay.core.data.di.PlatformDependentDataModule
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor

class NativePlatformDependentDataModule : PlatformDependentDataModule() {
    override fun bindsNetworkMonitor(): NetworkMonitor {
        return object : NetworkMonitor {
            override val isOnline: Flow<Boolean>
                get() = flowOf(true)
        }
    }

    override fun bindsTimeZoneMonitor(): TimeZoneMonitor {
        return object : TimeZoneMonitor {
            override val currentTimeZone: Flow<TimeZone>
                get() = flowOf(TimeZone.currentSystemDefault())
        }
    }
}
