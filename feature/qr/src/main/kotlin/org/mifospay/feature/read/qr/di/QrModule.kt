package org.mifospay.feature.read.qr.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.read.qr.ReadQrViewModel
import org.mifospay.feature.read.qr.utils.ScanQr

val QrModule = module {
    single {
        ScanQr()
    }
    viewModel {
        ReadQrViewModel(useCaseHandler = get(), scanQrUseCase = get())
    }

}