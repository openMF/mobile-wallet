/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.analytics.di

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module
import org.mifospay.core.analytics.AnalyticsHelper

val AnalyticsModule = module {

    single {
        Firebase.analytics
    }
    single<AnalyticsHelper> {
        FirebaseAnalyticsHelper(firebaseAnalytics = get())
    }
}
