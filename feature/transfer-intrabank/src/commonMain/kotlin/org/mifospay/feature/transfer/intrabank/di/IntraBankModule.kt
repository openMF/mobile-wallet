/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transfer.intrabank.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.transfer.intrabank.confirm.TransferConfirmViewModel
import org.mifospay.feature.transfer.intrabank.hub.IntraBankHubViewModel
import org.mifospay.feature.transfer.intrabank.selectScreen.SelectScreenViewModel

val IntraBankModule = module {
    viewModelOf(::IntraBankHubViewModel)
    viewModelOf(::SelectScreenViewModel)
    viewModelOf(::TransferConfirmViewModel)
}
