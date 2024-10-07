/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.mifospay.core.common.di.DispatchersModule
import org.mifospay.core.data.di.RepositoryModule
import org.mifospay.core.datastore.di.PreferencesModule
import org.mifospay.core.domain.di.DomainModule
import org.mifospay.core.network.di.LocalModule
import org.mifospay.core.network.di.NetworkModule
import org.mifospay.feature.auth.di.AuthModule
import org.mifospay.shared.MifosPayViewModel

object KoinModules {
    private val commonModules = module {
        includes(DispatchersModule)
    }
    private val dataModules = module {
        includes(RepositoryModule)
    }
    private val domainModules = module {
        includes(DomainModule)
    }
    private val coreDataStoreModules = module {
        includes(PreferencesModule)
    }
    private val networkModules = module {
        includes(LocalModule, NetworkModule)
    }
    private val sharedModule = module {
        viewModelOf(::MifosPayViewModel)
    }
    private val featureModules = module {
        includes(
            AuthModule,
        )
    }

    val allModules = listOf(
        commonModules,
        dataModules,
        domainModules,
        coreDataStoreModules,
        networkModules,
        featureModules,
        sharedModule,
    )
}

fun koinConfiguration() = koinApplication {
    modules(KoinModules.allModules)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(KoinModules.allModules)
    }
}
