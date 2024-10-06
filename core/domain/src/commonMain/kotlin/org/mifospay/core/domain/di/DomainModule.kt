/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.domain.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.common.MifosDispatchers
import org.mifospay.core.domain.LoginUseCase

val DomainModule = module {
    single {
        LoginUseCase(
            repository = get(),
            clientRepository = get(),
            userPreferencesRepository = get(),
            ioDispatcher = get(named(MifosDispatchers.IO.name)),
        )
    }
}
