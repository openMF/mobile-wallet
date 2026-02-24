/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import org.mifospay.core.data.util.AppLockOption

interface ChooseAuthOptionRepository {
    fun setAuthOption(option: AppLockOption)

    fun getAuthOption(): AppLockOption

    fun removeAuthOption()

    fun saveBiometricRegistrationData(registrationData: String)
}
