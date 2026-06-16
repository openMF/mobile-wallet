/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mifospay.core.data.repository.WidgetManagerRepository

class FinanceWidgetReceiver :
    GlanceAppWidgetReceiver(),
    KoinComponent {

    override val glanceAppWidget = FinanceGlanceWidget

    private val widgetManager: WidgetManagerRepository by inject()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        widgetManager.schedule()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        widgetManager.cancel()
    }
}
