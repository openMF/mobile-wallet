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

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import org.mifospay.core.common.DataState
import org.mifospay.core.model.instance.InstanceType
import org.mifospay.core.model.instance.InstancesConfig
import org.mifospay.core.model.instance.ServerInstance

interface InstanceConfigLoader {
    suspend fun fetchInstancesConfig(): DataState<InstancesConfig>
    fun observeInstancesConfig(): Flow<DataState<InstancesConfig>>
}

class FirebaseInstanceConfigLoader(
    private val ioDispatcher: CoroutineDispatcher,
    private val json: Json,
) : InstanceConfigLoader {
    private val remoteConfig = Firebase.remoteConfig

    companion object {
        private const val INSTANCES_CONFIG_KEY = "instances_config_v1"
        private const val FETCH_INTERVAL_SECONDS = 3600L // 1 hour

        // Default configuration as fallback
        private val DEFAULT_CONFIG = InstancesConfig(
            instances = listOf(
                ServerInstance(
                    endpoint = "mifos-bank-2.mifos.community",
                    protocol = "https://",
                    path = "/fineract-provider/api/v1/",
                    platformTenantId = "mifos-bank-2",
                    label = "Mifos Bank 2 Instance",
                    type = InstanceType.MAIN,
                    isDefault = true,
                ),
                ServerInstance(
                    endpoint = "mifos-bank-1.mifos.community",
                    protocol = "https://",
                    path = "/fineract-provider/api/v1/",
                    platformTenantId = "mifos-bank-1",
                    label = "Mifos Bank 1 Instance",
                    type = InstanceType.MAIN,
                    isDefault = false,
                ),
                ServerInstance(
                    endpoint = "apis.flexcore.mx",
                    protocol = "https://",
                    path = "/v1.0/vnext2/",
                    platformTenantId = "mifos-bank-2",
                    label = "Mifos Bank 2 Interbank Instance",
                    type = InstanceType.INTERBANK,
                    isDefault = true,
                ),
                ServerInstance(
                    endpoint = "apis.flexcore.mx",
                    protocol = "https://",
                    path = "/v1.0/vnext1/",
                    platformTenantId = "mifos-bank-1",
                    label = "Mifos Bank 1 Interbank Instance",
                    type = InstanceType.INTERBANK,
                    isDefault = false,
                ),
            ),
        )
    }

    override suspend fun fetchInstancesConfig(): DataState<InstancesConfig> {
        return try {
            // Configure Remote Config settings
            remoteConfig.settings {
                minimumFetchIntervalInSeconds = FETCH_INTERVAL_SECONDS
            }

            // Fetch and activate the latest config
            remoteConfig.fetchAndActivate()

            // Get the config value
            val configJson = remoteConfig.getValue(INSTANCES_CONFIG_KEY).asString()

            if (configJson.isBlank()) {
                return DataState.Success(DEFAULT_CONFIG)
            }

            // Parse the JSON
            val config = json.decodeFromString<InstancesConfig>(configJson)

            // Validate that there's at least one instance
            if (config.instances.isEmpty()) {
                return DataState.Success(DEFAULT_CONFIG)
            }

            DataState.Success(config)
        } catch (e: Exception) {
            // Return default config on error
            DataState.Success(DEFAULT_CONFIG)
        }
    }

    override fun observeInstancesConfig(): Flow<DataState<InstancesConfig>> = flow {
        emit(fetchInstancesConfig())
    }.flowOn(ioDispatcher)
}
