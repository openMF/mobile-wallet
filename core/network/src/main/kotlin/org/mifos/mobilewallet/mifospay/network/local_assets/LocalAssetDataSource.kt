package org.mifos.mobilewallet.mifospay.network.local_assets

import com.mifos.mobilewallet.model.City
import com.mifos.mobilewallet.model.Country
import com.mifos.mobilewallet.model.State

interface LocalAssetDataSource {

    suspend fun getCountries(): List<Country>

    suspend fun getStateList(): List<State>

    suspend fun getBanks(): List<String>

    suspend fun getCities(): List<City>
}
