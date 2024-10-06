/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.mifospay.core.data.NativePlatformDependentDataModule

actual val getPlatformDataModule: PlatformDependentDataModule
    get() = NativePlatformDependentDataModule()

actual val platformModule: Module
    get() = module {
        single<PlatformDependentDataModule> { getPlatformDataModule }
    }
