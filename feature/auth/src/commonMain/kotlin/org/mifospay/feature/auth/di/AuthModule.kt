/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifospay.feature.auth.login.LoginViewModel
import org.mifospay.feature.auth.mobileVerify.MobileVerificationViewModel
import org.mifospay.feature.auth.signup.SignupViewModel

val AuthModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::MobileVerificationViewModel)
}
