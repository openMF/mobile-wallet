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
