package org.mifospay.core.data.repository.local

import com.mifospay.core.model.City
import com.mifospay.core.model.Country
import com.mifospay.core.model.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.network.Dispatcher
import org.mifospay.core.network.MifosDispatchers
import org.mifospay.core.network.local_assets.MifosLocalAssetDataSource
import javax.inject.Inject

/**
 * Local implementation of the [LocalAssetRepository] that retrieves the countries, banks, cities
 * and state list from a JSON String.
 *
 */
class MifosLocalAssetRepository @Inject constructor(
    @Dispatcher(MifosDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: MifosLocalAssetDataSource,
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
