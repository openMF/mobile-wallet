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
import org.mifospay.core.data.repository.ChooseAuthOptionRepository

const val MIFOS_PASSCODE = "org.mifospay.mifos.passcode"

class MifosPasscodeAdapterImpl(
    private val settings: Settings,
    private val chooseAuthOptionRepository: ChooseAuthOptionRepository,
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
        chooseAuthOptionRepository.removeAuthOption()
    }
}
