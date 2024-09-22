/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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
