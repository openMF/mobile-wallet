package org.mifospay.feature.bank.accounts.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.core.data.di.DataModule
import org.mifospay.feature.bank.accounts.AccountViewModel
import org.mifospay.feature.bank.accounts.link.LinkBankAccountViewModel

val AccountsModule = module {
    includes(DataModule)
    viewModel {
        LinkBankAccountViewModel(localAssetRepository = get())
    }
    viewModel {
        AccountViewModel()
    }
}