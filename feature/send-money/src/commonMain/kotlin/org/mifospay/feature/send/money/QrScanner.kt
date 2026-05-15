package org.mifospay.feature.send.money

import kotlinx.coroutines.flow.Flow
import org.koin.core.module.Module

interface QrScanner {
    fun startScanning(): Flow<String?>
}

expect val ScannerModule: Module
