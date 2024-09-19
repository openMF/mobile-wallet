package org.mifospay.feature.home.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.domain.usecase.history.TransactionsHistory
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.feature.home.HomeViewModel

val HomeModule = module{
//    single { UseCaseHandler(get()) }
//    single { LocalRepository(get()) }
//    single { PreferencesHelper(get()) }
//    single { FetchAccount(get(),get()) }
//    single { TransactionsHistory(get(),get(),get(),get()) }
    viewModel {
        HomeViewModel(useCaseHandler = get(), localRepository = get(), preferencesHelper = get(),
            fetchAccountUseCase = get(), transactionsHistory = get())
    }

}