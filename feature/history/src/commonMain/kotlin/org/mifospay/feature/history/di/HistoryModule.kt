/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.history.di

import com.mobilebytelabs.kmptoolkit.pdfgenerator.ExperimentalPdfGeneratorApi
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.history.HistoryViewModel
import org.mifospay.feature.history.detail.TransactionDetailViewModel
import org.mifospay.feature.history.transactions.SpecificTransactionsViewModel

@OptIn(ExperimentalPdfGeneratorApi::class)
val HistoryModule = module {
    includes(HistoryPdfModule)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::SpecificTransactionsViewModel)
    viewModelOf(::TransactionDetailViewModel)
}
