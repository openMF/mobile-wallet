/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.mpay.qr.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.common.MifosDispatchers
import org.mifospay.feature.mpay.qr.MpayQrViewModel

val MpayQrModule = module {
    factory {
        MpayQrViewModel(
            localRepository = get(),
            repository = get(),
            accountRepository = get(),
            savedStateHandle = get(),
            ioDispatcher = get(named(MifosDispatchers.IO.name)),
        )
    }
}
