package org.mifospay.core.network.local_assets

import com.mifospay.core.model.City
import com.mifospay.core.model.Country
import com.mifospay.core.model.State

interface LocalAssetDataSource {

    suspend fun getCountries(): List<Country>

    suspend fun getStateList(): List<State>

    suspend fun getBanks(): List<String>

    suspend fun getCities(): List<City>
}
