/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.merchants.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.merchants.MerchantTransferViewModel
import org.mifospay.feature.merchants.MerchantViewModel

val MerchantsModule = module {
    viewModelOf(::MerchantViewModel)
    viewModelOf(::MerchantTransferViewModel)
}
