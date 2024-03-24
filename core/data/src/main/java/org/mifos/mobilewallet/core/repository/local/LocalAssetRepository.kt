package org.mifos.mobilewallet.core.repository.local

import com.mifos.mobilewallet.model.City
import com.mifos.mobilewallet.model.Country
import com.mifos.mobilewallet.model.State
import kotlinx.coroutines.flow.Flow

interface LocalAssetRepository {

    fun getCountries(): Flow<List<Country>>

    fun getStateList(): Flow<List<State>>

    fun getBanks(): Flow<List<String>>

    fun getCities(): Flow<List<City>>
}
