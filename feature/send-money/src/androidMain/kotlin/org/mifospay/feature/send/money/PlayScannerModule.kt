/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val PlayScannerModule = module {

    single {
        GmsBarcodeScanning.getClient(androidContext())
    }

    single {
        ModuleInstall.getClient(androidContext())
    }

    single<QrScanner> {
        QrScannerImp(
            scanner = get(),
            playModule = get(),
        )
    }
}
