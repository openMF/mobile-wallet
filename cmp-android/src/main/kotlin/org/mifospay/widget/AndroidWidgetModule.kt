package org.mifospay.widget

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.mifospay.core.data.util.WidgetSyncService

/**
 * Android-only Koin module.
 *
 * Binds [WidgetSyncService] (interface in :core:data) to its Android
 * implementation [AndroidWidgetSyncService].
 *
 * Loaded alongside [org.mifospay.shared.di.KoinModules.allModules] in
 * Application.onCreate so the `:core:domain` use cases that depend on
 * [WidgetSyncService] can resolve the binding.
 */
val androidWidgetModule = module {

    single<WidgetSyncService> {
        AndroidWidgetSyncService(context = get(), widgetDataProvider = get())
    }
}
