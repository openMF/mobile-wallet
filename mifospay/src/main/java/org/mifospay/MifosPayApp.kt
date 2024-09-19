/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.mifospay.di.KoinModules

class MifosPayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val koinModules = KoinModules()

        startKoin {
            printLogger(Level.ERROR)
            androidContext(this@MifosPayApp)
            modules(
                listOf(
                    koinModules.dataModules,
                    koinModules.mifosPayModule,
                    koinModules
                        .coreDataStoreModules,
                    koinModules.featureModules,
                    koinModules.networkModules,
                    koinModules
                        .analyticsModules,
                    koinModules.commonModules,
                    koinModules.libsModule
                ),
            )
        }

    }
}
