/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.accounts.di

import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.accounts.AccountViewModel
import org.mifospay.feature.accounts.savingsaccount.AddEditSavingViewModel
import org.mifospay.feature.accounts.savingsaccount.details.SavingAccountDetailViewModel

val AccountsModule = module {
    single<Json> { Json { ignoreUnknownKeys = true } }
    viewModelOf(::AccountViewModel)
    viewModelOf(::SavingAccountDetailViewModel)
    viewModelOf(::AddEditSavingViewModel)
}
