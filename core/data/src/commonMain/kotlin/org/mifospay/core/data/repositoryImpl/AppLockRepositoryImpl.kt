/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import com.russhwolf.settings.Settings
import org.mifospay.core.data.repository.AppLockRepository

const val APP_LOCK_FLAG = "org.mifospay.mifos.app_lock_flag"
class AppLockRepositoryImpl(
    private val settings: Settings,
) : AppLockRepository {
    override fun lockApp() {
        settings.putBoolean(APP_LOCK_FLAG, true)
    }

    override fun unlockApp() {
        settings.putBoolean(APP_LOCK_FLAG, false)
    }

    override fun deleteLock() {
        settings.remove(APP_LOCK_FLAG)
    }

    override fun isAppLocked(): Boolean {
        return settings.getBoolean(APP_LOCK_FLAG, true)
    }
}
