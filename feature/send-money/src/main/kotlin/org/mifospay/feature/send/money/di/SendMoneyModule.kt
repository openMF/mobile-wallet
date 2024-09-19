package org.mifospay.feature.send.money.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.send.money.SendPaymentViewModel


val SendMoneyModule = module{

    viewModel {
        SendPaymentViewModel(useCaseHandler = get(), localRepository = get(), fetchAccount = get())
    }

}