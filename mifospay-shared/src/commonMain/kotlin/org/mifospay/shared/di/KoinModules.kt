/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.mifos.library.passcode.di.PasscodeModule
import org.mifospay.core.common.di.DispatchersModule
import org.mifospay.core.data.di.RepositoryModule
import org.mifospay.core.datastore.di.PreferencesModule
import org.mifospay.core.domain.di.DomainModule
import org.mifospay.core.network.di.LocalModule
import org.mifospay.core.network.di.NetworkModule
import org.mifospay.feature.accounts.di.AccountsModule
import org.mifospay.feature.auth.di.AuthModule
import org.mifospay.feature.editpassword.di.EditPasswordModule
import org.mifospay.feature.faq.di.FaqModule
import org.mifospay.feature.history.di.HistoryModule
import org.mifospay.feature.home.di.HomeModule
import org.mifospay.feature.invoices.di.InvoicesModule
import org.mifospay.feature.kyc.di.KYCModule
import org.mifospay.feature.make.transfer.di.MakeTransferModule
import org.mifospay.feature.merchants.di.MerchantsModule
import org.mifospay.feature.notification.di.NotificationModule
import org.mifospay.feature.payments.di.PaymentsModule
import org.mifospay.feature.profile.di.ProfileModule
import org.mifospay.feature.qr.di.QrModule
import org.mifospay.feature.receipt.di.ReceiptModule
import org.mifospay.feature.request.money.di.RequestMoneyModule
import org.mifospay.feature.savedcards.di.SavedCardsModule
import org.mifospay.feature.send.money.di.SendMoneyModule
import org.mifospay.feature.settings.di.SettingsModule
import org.mifospay.feature.standing.instruction.di.StandingInstructionModule
import org.mifospay.feature.upi.setup.di.UpiSetupModule
import org.mifospay.shared.MifosPayViewModel

object KoinModules {
    private val commonModules = module {
        includes(DispatchersModule)
    }
    private val dataModules = module {
        includes(RepositoryModule)
    }
    private val domainModules = module {
        includes(DomainModule)
    }
    private val coreDataStoreModules = module {
        includes(PreferencesModule)
    }
    private val networkModules = module {
        includes(LocalModule, NetworkModule)
    }
    private val sharedModule = module {
        viewModelOf(::MifosPayViewModel)
    }
    private val featureModules = module {
        includes(
            AuthModule,
            HomeModule,
            SettingsModule,
            FaqModule,
            EditPasswordModule,
            ProfileModule,
            HistoryModule,
            PaymentsModule,
            AccountsModule,
            InvoicesModule,
            KYCModule,
            NotificationModule,
            SavedCardsModule,
            ReceiptModule,
            StandingInstructionModule,
            RequestMoneyModule,
            SendMoneyModule,
            MakeTransferModule,
            QrModule,
            MerchantsModule,
            UpiSetupModule,
        )
    }
    private val LibraryModule = module {
        includes(PasscodeModule)
    }

    val allModules = listOf(
        commonModules,
        dataModules,
        domainModules,
        coreDataStoreModules,
        networkModules,
        featureModules,
        sharedModule,
        LibraryModule,
    )
}

fun koinConfiguration() = koinApplication {
    modules(KoinModules.allModules)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(KoinModules.allModules)
    }
}
