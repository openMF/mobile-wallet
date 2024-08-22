package org.mifospay.shared.di

import android.content.Context
import org.koin.dsl.module
import org.mifospay.shared.core.data.PreferencesHelper

actual val platformModule = module {
    single { PreferencesHelper(get<Context>()) }
}