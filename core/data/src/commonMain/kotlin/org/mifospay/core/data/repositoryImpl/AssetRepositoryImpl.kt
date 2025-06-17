/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.serialization.json.Json
import mobile_wallet.core.data.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.AssetRepository
import org.mifospay.core.model.utils.Country

class AssetRepositoryImpl : AssetRepository {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getCountriesWithStates(): DataState<Map<String, List<String>>> {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val bytes = Res.readBytes("files/countries.json")
            val jsonString = bytes.decodeToString()
            val countries = json.decodeFromString<List<Country>>(jsonString)
            DataState.Success(
                countries.associate { country ->
                    country.name to country.states.map { it.name }
                },
            )
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
