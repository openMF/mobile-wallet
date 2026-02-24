/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import com.russhwolf.settings.Settings
import org.mifospay.core.data.repository.PlatformAuthenticationDataRepository

const val BIOMETRIC_REGISTRATION_KEY = "org.mifos.mifospay.biometric.registration.key"

class PlatformAuthenticationDataRepositoryImpl(
    private val settings: Settings,
) : PlatformAuthenticationDataRepository {
    override fun saveBiometricRegistrationData(registrationData: String) {
        settings.putString(BIOMETRIC_REGISTRATION_KEY, registrationData)
    }

    override fun getBiometricRegistrationData(): String {
        return settings.getString(BIOMETRIC_REGISTRATION_KEY, "")
    }

    override fun clearBiometricRegistrationData() {
        settings.remove(BIOMETRIC_REGISTRATION_KEY)
    }
}
