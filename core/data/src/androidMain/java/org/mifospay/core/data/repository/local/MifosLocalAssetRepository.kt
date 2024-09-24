/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository.local

import com.mifospay.core.model.City
import com.mifospay.core.model.Country
import com.mifospay.core.model.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mifos.core.network.localAssets.LocalAssetDataSource

/**
 * Local implementation of the [LocalAssetRepository] that retrieves the countries, banks, cities
 * and state list from a JSON String.
 *
 */

class MifosLocalAssetRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val datasource: LocalAssetDataSource,
) : LocalAssetRepository {

    override fun getCountries(): Flow<List<Country>> = flow {
        emit(datasource.getCountries())
    }.flowOn(ioDispatcher)

    override fun getStateList(): Flow<List<State>> = flow {
        emit(datasource.getStateList())
    }.flowOn(ioDispatcher)

    override fun getBanks(): Flow<List<String>> = flow {
        emit(datasource.getBanks())
    }.flowOn(ioDispatcher)

    override fun getCities(): Flow<List<City>> = flow {
        emit(datasource.getCities())
    }.flowOn(ioDispatcher)
}
