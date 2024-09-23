/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.faq.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.faq.FAQViewModel

<<<<<<<< HEAD:feature/faq/src/main/kotlin/org/mifospay/feature/faq/di/FaqModule.kt
val FaqModule = module {

    viewModel {
        FAQViewModel()
    }
}
========
dependencies { }
>>>>>>>> 35f52055 (Migrating from hilt to koin (This) (#1764)):feature/search/build.gradle.kts
