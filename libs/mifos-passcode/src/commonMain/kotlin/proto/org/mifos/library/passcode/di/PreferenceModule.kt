/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package proto.org.mifos.library.passcode.di

import com.russhwolf.settings.Settings
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.common.MifosDispatchers
import proto.org.mifos.library.passcode.data.PasscodeManager
import proto.org.mifos.library.passcode.data.PasscodePreferencesDataSource

val PasscodePreferenceModule = module {
    factory<Settings> { Settings() }
    factory { PasscodePreferencesDataSource(get(), get(named(MifosDispatchers.IO.name))) }
    factory { PasscodeManager(get(), get(named(MifosDispatchers.Unconfined.name))) }
}
