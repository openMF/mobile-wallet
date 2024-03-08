package org.mifos.mobilewallet.mifospay.network.local_assets

import com.mifos.mobilewallet.model.City
import com.mifos.mobilewallet.model.Country
import com.mifos.mobilewallet.model.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.mifos.mobilewallet.mifospay.core.network.Dispatcher
import org.mifos.mobilewallet.mifospay.core.network.MifosDispatchers
import org.mifos.mobilewallet.mifospay.network.JvmLocalAssetManager
import javax.inject.Inject

class MifosLocalAssetDataSource @Inject constructor(
    @Dispatcher(MifosDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: LocalAssetManager = JvmLocalAssetManager,
) : LocalAssetDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getCountries(): List<Country> {
        return withContext(ioDispatcher) {
            assets.open(COUNTRIES_ASSET).use(networkJson::decodeFromStream)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getStateList(): List<State> {
        return withContext(ioDispatcher) {
            assets.open(STATES_ASSET).use(networkJson::decodeFromStream)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getBanks(): List<String> {
        return withContext(ioDispatcher) {
            assets.open(BANKS_ASSET).use(networkJson::decodeFromStream)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getCities(): List<City> {
        return withContext(ioDispatcher) {
            assets.open(CITIES_ASSET).use(networkJson::decodeFromStream)
        }
    }

    companion object {
        private const val COUNTRIES_ASSET = "countries.json"
        private const val STATES_ASSET = "states.json"
        private const val CITIES_ASSET = "cities.json"
        private const val BANKS_ASSET = "banks.json"
        private const val COUNTRIES_TO_CITIES_ASSET = "countriesToCities.json"
    }
}
