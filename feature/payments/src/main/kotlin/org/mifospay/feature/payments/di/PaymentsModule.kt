package org.mifospay.feature.payments.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.payments.TransferViewModel

val PaymentsModule = module{

    viewModel {

        TransferViewModel(mUsecaseHandler = get(), localRepository = get(), mFetchAccount = get())
         }

}