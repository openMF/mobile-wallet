package org.mifospay.feature.bank.accounts.di

import android.util.Log
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.core.data.di.DataModule
import org.mifospay.core.data.repository.local.LocalAssetRepository
import org.mifospay.core.data.repository.local.MifosLocalAssetRepository
import org.mifospay.feature.bank.accounts.AccountViewModel
import org.mifospay.feature.bank.accounts.link.LinkBankAccountViewModel

val AccountsModule = module {
    includes(DataModule)
    viewModel {
        try {
            LinkBankAccountViewModel(localAssetRepository = get())
        } catch (e: NoDefinitionFoundException) {
            Log.e("KoinError", "Missing Koin definition for LocalAssetRepository: ${e.message}")
            Log.e("KoinError", "Missing Koin definition for LocalAssetRepository: ${e}")

            throw e // Re-throw the error so Koin still fails
        }
//        LinkBankAccountViewModel(localAssetRepository = get())
    }
    viewModel {
        AccountViewModel()
    }
}