/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.mifospay.core.network.BaseURL
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.KtorfitClient
import org.mifospay.core.network.SelfServiceApiManager

val NetworkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single<KtorfitClient>(BaseClient) {
        KtorfitClient.builder()
            .baseURL(BaseURL.url)
            .build()
    }

    single(SelfClient) {
        KtorfitClient.builder()
            .baseURL(BaseURL.selfServiceUrl)
            .build()
    }

    single {
        FineractApiManager(
            ktorfitClient = get(BaseClient),
        )
    }

    single {
        SelfServiceApiManager(
            ktorfitClient = get(SelfClient),
        )
    }
}
