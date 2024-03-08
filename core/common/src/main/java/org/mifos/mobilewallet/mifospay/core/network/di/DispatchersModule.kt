package org.mifos.mobilewallet.mifospay.core.network.di

import org.mifos.mobilewallet.mifospay.core.network.Dispatcher
import org.mifos.mobilewallet.mifospay.core.network.MifosDispatchers.Default
import org.mifos.mobilewallet.mifospay.core.network.MifosDispatchers.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
