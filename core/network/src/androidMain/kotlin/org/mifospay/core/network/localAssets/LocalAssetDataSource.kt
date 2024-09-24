/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.localAssets

import com.mifospay.core.model.City
import com.mifospay.core.model.Country
import com.mifospay.core.model.State

interface LocalAssetDataSource {

    suspend fun getCountries(): List<Country>

    suspend fun getStateList(): List<State>

    suspend fun getBanks(): List<String>

    suspend fun getCities(): List<City>
}
