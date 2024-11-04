/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val PlayScannerModule = module {
    single<Context> {
        androidApplication().applicationContext
    }

    single<GmsBarcodeScannerOptions> {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .allowManualInput()
            .build()
    }

    single<ModuleInstallClient> {
        ModuleInstall.getClient(get<Context>())
    }

    single<GmsBarcodeScanner> {
        GmsBarcodeScanning.getClient(get<Context>(), get())
    }

    single<QrScanner> {
        QrScannerImp(get(), get())
    }
}
