/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transfer.interbank.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.transfer.interbank.InterbankTransferViewModel
// from #1906 pr
//import org.mifospay.feature.send.money.PayAnyoneViewModel
//import org.mifospay.feature.send.money.PayeeDetailsViewModel
//import org.mifospay.feature.send.money.ScannerModule
//import org.mifospay.feature.send.money.SendMoneyOptionsViewModel
//import org.mifospay.feature.send.money.SendMoneyViewModel

val interbankTransferModule = module {
    viewModelOf(::InterbankTransferViewModel)

    // from #1906 pr
//    includes(ScannerModule)
//    viewModelOf(::SendMoneyViewModel)
//    viewModelOf(::SendMoneyOptionsViewModel)
//    viewModelOf(::PayeeDetailsViewModel)
//    viewModelOf(::PayAnyoneViewModel)
}
