package org.mifospay.feature.request.money.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.request.money.GenerateQr
import org.mifospay.feature.request.money.ShowQrViewModel

val RequestMoneyModule = module {
    single {
        GenerateQr()
    }
    viewModel {
        ShowQrViewModel(mUseCaseHandler = get(), generateQrUseCase = get(), mPreferencesHelper =
        get(), savedStateHandle = get())
    }

}