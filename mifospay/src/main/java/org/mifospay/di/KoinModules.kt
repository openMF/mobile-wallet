package org.mifospay.di

import org.koin.dsl.module
import org.mifos.library.passcode.di.ApplicationModule
import org.mifospay.core.analytics.di.AnalyticsModule
import org.mifospay.core.data.di.DataModule
import org.mifospay.core.data.di.LocalDataModule
import org.mifospay.core.datastore.di.CoreDataStoreModule
import org.mifospay.core.network.di.CoroutineScopesModule
import org.mifospay.core.network.di.DispatchersModule
import org.mifospay.core.network.di.LocalModule
import org.mifospay.core.network.di.NetworkModule
import org.mifospay.feature.auth.di.AuthModule
import org.mifospay.feature.bank.accounts.di.AccountsModule
import org.mifospay.feature.di.HistoryModule
import org.mifospay.feature.editpassword.di.EditPasswordModule
import org.mifospay.feature.faq.di.FaqModule
import org.mifospay.feature.home.di.HomeModule
import org.mifospay.feature.invoices.di.InvoicesModule
import org.mifospay.feature.kyc.di.KYCModule
import org.mifospay.feature.make.transfer.di.MakeTransferModule
import org.mifospay.feature.merchants.di.MerchantsModule
import org.mifospay.feature.notification.di.NotificationModule
import org.mifospay.feature.payments.di.PaymentsModule
import org.mifospay.feature.profile.di.ProfileModule
import org.mifospay.feature.read.qr.di.QrModule
import org.mifospay.feature.receipt.di.ReceiptModule
import org.mifospay.feature.request.money.di.RequestMoneyModule
import org.mifospay.feature.savedcards.di.SavedCardsModule
import org.mifospay.feature.search.di.SearchModule
import org.mifospay.feature.send.money.di.SendMoneyModule
import org.mifospay.feature.settings.di.SettingsModule
import org.mifospay.feature.standing.instruction.di.StandingInstructionModule
import org.mifospay.feature.upiSetup.di.UpiSetupModule

class KoinModules {
    val analyticsModules= module {
        includes(AnalyticsModule)
    }
    val commonModules = module {
            includes(CoroutineScopesModule,DispatchersModule)
    }
    val dataModules = module {
            includes(DataModule,LocalDataModule)
    }
    val coreDataStoreModules = module {
        includes(CoreDataStoreModule)
    }
    val networkModules = module{
       includes(LocalModule, NetworkModule)
    }
    val featureModules = module {
        includes(AuthModule,AccountsModule,EditPasswordModule,FaqModule,HistoryModule,HomeModule,
        InvoicesModule,KYCModule,MakeTransferModule,MerchantsModule,NotificationModule,
        PaymentsModule,ProfileModule,QrModule,ReceiptModule,RequestMoneyModule,
        SavedCardsModule,SearchModule,SendMoneyModule,SettingsModule,
        StandingInstructionModule,UpiSetupModule)
    }
    val mifosPayModule= module {
        includes(JankStatsModule)
    }
    val libsModule = module {
        includes(ApplicationModule)
    }
}