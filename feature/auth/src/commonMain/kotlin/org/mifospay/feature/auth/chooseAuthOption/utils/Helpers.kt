/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.chooseAuthOption.utils

import org.mifospay.feature.auth.chooseAuthOption.AppLockOption

object Helpers {

    fun authOptionToStringMapperFunction(option: AppLockOption): String {
        return when (option) {
            AppLockOption.MifosPasscode -> Constants.MIFOS_PASSCODE_VALUE
            AppLockOption.DeviceLock -> Constants.DEVICE_AUTHENTICATION_METHOD_VALUE
            AppLockOption.None -> ""
        }
    }

    fun stringToAuthOptionMapperFunction(option: String): AppLockOption {
        return when (option) {
            Constants.MIFOS_PASSCODE_VALUE -> AppLockOption.MifosPasscode
            Constants.DEVICE_AUTHENTICATION_METHOD_VALUE -> AppLockOption.DeviceLock
            else -> AppLockOption.None
        }
    }
}
