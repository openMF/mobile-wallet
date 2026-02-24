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
import org.mifospay.core.data.repository.ChooseAuthOptionRepository
import org.mifospay.core.data.repository.PlatformAuthenticationDataRepository
import org.mifospay.core.data.util.AppLockOption
import org.mifospay.core.data.util.Helpers

const val APP_LOCK_KEY = "auth_method"

class ChooseAuthOptionRepositoryImpl(
    private val settings: Settings,
    private val platformAuthenticationDataRepository: PlatformAuthenticationDataRepository,
) : ChooseAuthOptionRepository {
    override fun setAuthOption(option: AppLockOption) {
        settings.putString(
            APP_LOCK_KEY,
            Helpers.authOptionToStringMapperFunction(option),
        )
    }

    override fun getAuthOption(): AppLockOption {
        val option = settings.getString(
            APP_LOCK_KEY,
            "",
        )

        return Helpers.stringToAuthOptionMapperFunction(option)
    }

    override fun removeAuthOption() {
        settings.remove(APP_LOCK_KEY)
    }

    override fun saveBiometricRegistrationData(registrationData: String) {
        platformAuthenticationDataRepository.saveBiometricRegistrationData(registrationData)
    }
}
