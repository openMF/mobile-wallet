package org.mifospay.core.data.repository.local

import com.mifospay.core.model.City
import com.mifospay.core.model.Country
import com.mifospay.core.model.State
import kotlinx.coroutines.flow.Flow

interface LocalAssetRepository {

    fun getCountries(): Flow<List<Country>>

    fun getStateList(): Flow<List<State>>

    fun getBanks(): Flow<List<String>>

    fun getCities(): Flow<List<City>>
}
