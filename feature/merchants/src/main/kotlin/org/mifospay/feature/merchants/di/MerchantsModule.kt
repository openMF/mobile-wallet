package org.mifospay.feature.merchants.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.merchants.MerchantTransferViewModel
import org.mifospay.feature.merchants.MerchantViewModel

val MerchantsModule = module {

    viewModel {
        MerchantViewModel(mUseCaseHandler = get(), mFetchMerchantsUseCase = get(),
            mUseCaseFactory = get(),mTaskLooper = get())
    }
    viewModel{
        MerchantTransferViewModel(mUseCaseHandler = get(), localRepository = get(),
            preferencesHelper = get(), transactionsHistory = get(), mUseCaseFactory = get(),
            mFetchAccount = get(), mTaskLooper = get(), savedStateHandle = get())
    }

}