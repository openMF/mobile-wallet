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
import androidx.metrics.performance.JankStats.OnFrameListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object JankStatsModule {
    @Provides
    fun providesOnFrameListener(): OnFrameListener = OnFrameListener { frameData ->
        // Make sure to only log janky frames.
        if (frameData.isJank) {
            // We're currently logging this but would better report it to a backend.
            Log.v("Mifos Jank", frameData.toString())
        }
    }

    @Provides
    fun providesWindow(activity: Activity): Window = activity.window

    @Provides
    fun providesJankStats(
        window: Window,
        frameListener: OnFrameListener,
    ): JankStats = JankStats.createAndTrack(window, frameListener)
}
