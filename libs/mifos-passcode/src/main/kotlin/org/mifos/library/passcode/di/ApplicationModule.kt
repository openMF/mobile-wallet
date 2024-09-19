/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.library.passcode.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.mifos.library.passcode.data.PasscodeRepository
import org.mifos.library.passcode.data.PasscodeRepositoryImpl
import org.mifos.library.passcode.utility.PreferenceManager
import org.koin.core.module.dsl.viewModel
import org.mifos.library.passcode.data.PasscodeManager
import org.mifos.library.passcode.viewmodels.PasscodeViewModel

val ApplicationModule = module {

    factory {
        PreferenceManager(context = androidContext())
    }

    single<PasscodeRepository> { PasscodeRepositoryImpl(preferenceManager = get()) }

    viewModel {
        PasscodeViewModel(passcodeRepository = get())
    }
    factory {
        PasscodeManager(passcodePreferencesHelper = get())
    }

}
