/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.config

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import mobile_wallet.core.network.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.mifospay.core.common.DataState
import org.mifospay.core.model.instance.InstancesConfig

class SupabaseInstanceConfigLoader(
    private val ioDispatcher: CoroutineDispatcher,
    private val json: Json,
) : InstanceConfigLoader {

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseCredentials.URL,
            supabaseKey = SupabaseCredentials.ANON_KEY,
        ) {
            install(Postgrest)
        }
    }

    override suspend fun fetchInstancesConfig(): DataState<InstancesConfig> {
        if (!SupabaseCredentials.isConfigured) {
            return loadFallbackConfig()
        }
        return try {
            val config = supabase.postgrest
                .from("app_config")
                .select()
                .decodeSingle<InstancesConfig>()
            DataState.Success(config)
        } catch (e: Exception) {
            loadFallbackConfig()
        }
    }

    override fun observeInstancesConfig(): Flow<DataState<InstancesConfig>> = flow {
        emit(fetchInstancesConfig())
    }.flowOn(ioDispatcher)

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadFallbackConfig(): DataState<InstancesConfig> {
        return try {
            val bytes = Res.readBytes("files/instances_config_default.json")
            val jsonString = bytes.decodeToString()
            val config = json.decodeFromString<InstancesConfig>(jsonString)
            DataState.Success(config)
        } catch (e: Exception) {
            DataState.Success(DEFAULT_CONFIG)
        }
    }

    companion object {
        private val DEFAULT_CONFIG = InstancesConfig(instances = emptyList())
    }
}
