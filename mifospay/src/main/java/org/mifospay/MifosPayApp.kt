/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.mifos.mobile.passcode.utils.ForegroundChecker
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by naman on 17/8/17.
 */
@HiltAndroidApp
class MifosPayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (instance == null) {
            instance = this
        }

        // Initialize ForegroundChecker
        ForegroundChecker.init(this)
    }

    companion object {
        private var instance: MifosPayApp? = null

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        operator fun get(context: Context): MifosPayApp {
            return context.applicationContext as MifosPayApp
        }

        @JvmStatic
        val context: Context?
            get() = instance
    }
}
