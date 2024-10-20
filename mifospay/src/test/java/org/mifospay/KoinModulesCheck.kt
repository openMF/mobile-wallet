/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.koin.test.AutoCloseKoinTest
import org.koin.test.verify.verify
import org.mifos.library.passcode.data.PasscodeManager
import org.mifospay.core.data.repository.auth.UserDataRepository
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.services.KtorAuthenticationService
import org.mifospay.di.KoinModules
import kotlin.test.Test

class KoinModulesCheck : AutoCloseKoinTest() {

    @Test
    fun checkKoinModules() {
        val koinModules = KoinModules()

        koinModules.libsModule.verify(
            extraTypes = listOf(Context::class),
        )
        koinModules.commonModules.verify()
        koinModules.analyticsModules.verify()
        koinModules.networkModules.verify(
            extraTypes = listOf(
                HttpClientEngine::class,
                HttpClientConfig::class,
            ),
        )
        koinModules.featureModules.verify(
            extraTypes = listOf(
                PreferencesHelper::class,
                FineractApiManager::class,
                SelfServiceApiManager::class,
                KtorAuthenticationService::class,
                SavedStateHandle::class,
                Context::class,
            ),
        )
        koinModules.coreDataStoreModules.verify(
            extraTypes = listOf(
                PreferencesHelper::class,
                Context::class,
            ),
        )
        koinModules.mifosPayModule.verify(
            extraTypes = listOf(
                UserDataRepository::class,
                PasscodeManager::class,
                Context::class,
            ),
        )
        koinModules.dataModules.verify(
            extraTypes = listOf(
                FineractApiManager::class,
                SelfServiceApiManager::class,
                KtorAuthenticationService::class,
                PreferencesHelper::class,
                Map::class,
            ),
        )
    }
}
