package org.mifospay.feature.make.transfer.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.make.transfer.MakeTransferViewModel

val MakeTransferModule = module {
    viewModel {

        MakeTransferViewModel(
            savedStateHandle = get(), useCaseHandler = get(),
            searchClientUseCase =
            get(),
            transferFundsUseCase = get(), localRepository = get(),
        )
    }


}