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
import org.mifos.authenticator.passcode.PasscodeStorageAdapter

const val MIFOS_PASSCODE = "org.mifospay.mifos.passcode"
const val REGISTRATION_DATA_KEY = "org.mifospay.mifos.registration_data"
const val BIOMETRIC_REGISTERED_KEY = "org.mifospay.mifos.biometric_registered"

class MifosPasscodeAdapterImpl(
    private val settings: Settings,
) : PasscodeStorageAdapter {
    override fun savePasscode(passcode: String) {
        settings.putString(MIFOS_PASSCODE, passcode)
    }

    override fun loadPasscode(): String? {
        val passcode = settings.getString(MIFOS_PASSCODE, "")
        if (passcode.isBlank()) return null
        return passcode
    }

    override fun deletePasscode() {
        settings.remove(MIFOS_PASSCODE)
    }

    override fun saveRegistrationData(registrationData: String) {
        settings.putBoolean(BIOMETRIC_REGISTERED_KEY, true)
        settings.putString(REGISTRATION_DATA_KEY, registrationData)
    }

    override fun loadRegistrationData(): String? {
        if (!settings.getBoolean(BIOMETRIC_REGISTERED_KEY, false)) return null
        return settings.getString(REGISTRATION_DATA_KEY, "")
    }

    override fun deleteRegistrationData() {
        settings.remove(BIOMETRIC_REGISTERED_KEY)
        settings.remove(REGISTRATION_DATA_KEY)
    }
}
