package org.mifospay.feature.receipt.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.receipt.ReceiptViewModel

val ReceiptModule = module{

    viewModel {
        ReceiptViewModel(mUseCaseHandler = get(), preferencesHelper = get(),
            downloadTransactionReceiptUseCase = get(), fetchAccountTransactionUseCase = get(),
            fetchAccountTransferUseCase = get(), savedStateHandle = get())
    }

}