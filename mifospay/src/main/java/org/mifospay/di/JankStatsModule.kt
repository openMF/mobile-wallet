/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.di

import android.app.Activity
import android.util.Log
import android.view.Window
import androidx.metrics.performance.JankStats
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.mifospay.MainActivityViewModel

val JankStatsModule = module {

    factory { (activity: Activity) -> activity.window }
    factory { (window: Window) ->
        JankStats.createAndTrack(window) { frameData ->
        // Make sure to only log janky frames.
            if (frameData.isJank) {
                // We're currently logging this but would better report it to a backend.
                Log.v("Mifos Jank", frameData.toString())
            }
        }
    }

    viewModel{
        MainActivityViewModel(userDataRepository = get(), passcodeManager = get())
    }
}
