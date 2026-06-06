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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.shared.di.KoinModules

class MifosPayApp : Application() {
    private val userDataRepository: UserPreferencesRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MifosPayApp)
            androidLogger()
            modules(KoinModules.allModules)
        }

        restoreSavedLanguage()
    }

    private fun restoreSavedLanguage() {
        runBlocking {
            val language = userDataRepository.language.first()

            // Convert the saved LanguageConfig to LocaleListCompat
            val desiredLocales = if (language.localeName != null) {
                LocaleListCompat.forLanguageTags(language.localeName)
            } else {
                // System default
                LocaleListCompat.getEmptyLocaleList()
            }

            // Only update if the current locale differs from saved preference
            val currentLocales = AppCompatDelegate.getApplicationLocales()
            if (currentLocales != desiredLocales) {
                AppCompatDelegate.setApplicationLocales(desiredLocales)
            }
        }
    }
}
