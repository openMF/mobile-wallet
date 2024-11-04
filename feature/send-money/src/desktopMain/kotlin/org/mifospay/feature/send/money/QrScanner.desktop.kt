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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.module.Module
import org.koin.dsl.module

class QrScannerImpl : QrScanner {
    override fun startScanning(): Flow<String?> {
        println("This function is not supported on desktop")
        return emptyFlow()
    }
}

actual val ScannerModule: Module
    get() = module { single<QrScanner> { QrScannerImpl() } }
