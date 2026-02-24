package org.mifospay.feature.authenticator.biometrics

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val AuthenticatorBiometricsModule = module {
    viewModelOf(::AuthenticationScreenViewModel)
}