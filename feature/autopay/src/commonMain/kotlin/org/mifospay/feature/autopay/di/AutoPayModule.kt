/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.autopay.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.autopay.AddBillViewModel
import org.mifospay.feature.autopay.AddBillerViewModel
import org.mifospay.feature.autopay.AutoPayPreferencesViewModel
import org.mifospay.feature.autopay.AutoPayScheduleDetailsViewModel
import org.mifospay.feature.autopay.AutoPayScheduleManagementViewModel
import org.mifospay.feature.autopay.AutoPayViewModel
import org.mifospay.feature.autopay.BillListViewModel
import org.mifospay.feature.autopay.BillerListViewModel
import org.mifospay.feature.autopay.EditBillViewModel
import org.mifospay.feature.autopay.EditBillerViewModel

val AutoPayModule = module {
    viewModelOf(::AutoPayViewModel)
    viewModelOf(::AutoPayScheduleDetailsViewModel)
    viewModelOf(::AddBillerViewModel)
    viewModelOf(::BillerListViewModel)
    viewModelOf(::EditBillerViewModel)
    viewModelOf(::AddBillViewModel)
    viewModelOf(::EditBillViewModel)
    viewModelOf(::BillListViewModel)
    viewModelOf(::AutoPayPreferencesViewModel)
    viewModelOf(::AutoPayScheduleManagementViewModel)
}
