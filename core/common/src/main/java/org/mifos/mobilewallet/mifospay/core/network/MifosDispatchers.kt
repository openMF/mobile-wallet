package org.mifos.mobilewallet.mifospay.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val mifosDispatcher: MifosDispatchers)

enum class MifosDispatchers {
    Default,
    IO,
}
