/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.history.HistoryViewModel
import org.mifospay.feature.history.detail.TransactionDetailViewModel
import org.mifospay.feature.history.transactions.SpecificTransactionsViewModel

val HistoryModule = module {
    viewModelOf(::HistoryViewModel)
    viewModelOf(::SpecificTransactionsViewModel)
    viewModelOf(::TransactionDetailViewModel)
}
