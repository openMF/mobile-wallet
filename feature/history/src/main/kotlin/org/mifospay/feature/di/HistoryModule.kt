/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.history.HistoryViewModel
import org.mifospay.feature.specific.transactions.SpecificTransactionsViewModel
import org.mifospay.feature.transaction.detail.TransactionDetailViewModel

val HistoryModule = module {

    viewModel {
        HistoryViewModel(
            mUseCaseHandler = get(),
            mLocalRepository = get(),
            mFetchAccountUseCase = get(),
            fetchAccountTransactionsUseCase = get(),
        )
    }

    viewModel {
        SpecificTransactionsViewModel(
            mUseCaseFactory = get(),
            mTaskLooper = get(),
            savedStateHandle = get(),
        )
    }

    viewModel {
        TransactionDetailViewModel(mUseCaseHandler = get(), mFetchAccountTransferUseCase = get())
    }
}
