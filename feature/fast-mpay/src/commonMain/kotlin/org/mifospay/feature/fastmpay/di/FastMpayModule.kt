/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.fastmpay.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.fastmpay.FastMpayProcessor
import org.mifospay.feature.fastmpay.FastMpayViewModel

val FastMpayModule = module {
    factory {
        // Phase-5 Batch-3: BeneficiaryRepository → SelfServiceRepository so the
        // processor's beneficiary read walks the batch-1 store-backed
        // getBeneficiaryListScreen(...) reader (offline-first via
        // wallet_beneficiaries Room SoT). No new BeneficiaryStore is created
        // for the fast-mpay surface — the batch-1 store is REUSED.
        FastMpayProcessor(
            selfServiceRepository = get(),
            userPreferencesRepository = get(),
            officeRepository = get(),
        )
    }
    viewModelOf(::FastMpayViewModel)
}
