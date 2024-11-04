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

import co.touchlab.kermit.Logger
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.koin.core.module.Module

class QrScannerImp(
    private val scanner: GmsBarcodeScanner,
    private val playModule: ModuleInstallClient,
) : QrScanner {
    init {
        playModule
            .areModulesAvailable(scanner)
            .addOnSuccessListener {
                if (!it.areModulesAvailable()) {
                    val newRequest = ModuleInstallRequest.newBuilder().addApi(scanner).build()
                    playModule.installModules(newRequest)
                }
            }.addOnFailureListener {
                Logger.d("Failed to install QRCodeScanner Module")
            }
    }

    override fun startScanning(): Flow<String?> {
        return callbackFlow {
            scanner.startScan()
                .addOnSuccessListener {
                    launch {
                        send(it.rawValue)
                    }
                }.addOnFailureListener {
                    launch {
                        send(null)
                    }
                }
            awaitClose { }
        }
    }
}

actual val ScannerModule: Module
    get() = PlayScannerModule
