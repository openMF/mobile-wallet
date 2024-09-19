package org.mifospay.feature.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.history.HistoryViewModel
import org.mifospay.feature.specific.transactions.SpecificTransactionsViewModel
import org.mifospay.feature.transaction.detail.TransactionDetailViewModel

val HistoryModule = module{

    viewModel {
        HistoryViewModel(mUseCaseHandler = get(), mLocalRepository = get(), mFetchAccountUseCase
        = get(), fetchAccountTransactionsUseCase = get())
    }

    viewModel {
        SpecificTransactionsViewModel(mUseCaseFactory = get(), mTaskLooper = get(),
            savedStateHandle = get())
    }

    viewModel {
        TransactionDetailViewModel(mUseCaseHandler = get(), mFetchAccountTransferUseCase = get())
    }
}