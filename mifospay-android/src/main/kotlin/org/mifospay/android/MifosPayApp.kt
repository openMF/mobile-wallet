/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.mifospay.shared.di.KoinModules

class MifosPayApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MifosPayApp)
            androidLogger()
            modules(KoinModules.allModules)
        }
    }
}
