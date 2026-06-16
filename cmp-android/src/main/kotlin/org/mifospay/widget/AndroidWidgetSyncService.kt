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
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import org.mifospay.core.data.util.WidgetSyncService
import org.mifospay.core.model.widget.WidgetData
import org.mifospay.shared.widget.WidgetDataProvider

class AndroidWidgetSyncService(
    private val context: Context,
    private val widgetDataProvider: WidgetDataProvider,
) : WidgetSyncService {

    override suspend fun syncToWidget(data: WidgetData) {
        FinanceGlanceWidget.updateAll(context)
    }

    override suspend fun refreshWidget() {
        // Invalidate first so the flow re-fetches fresh balance,
        // then trigger Glance to recompose.
        widgetDataProvider.invalidate()

        val manager = GlanceAppWidgetManager(context)
        manager.getGlanceIds(FinanceGlanceWidget::class.java)
            .forEach { id -> FinanceGlanceWidget.update(context, id) }
    }

    override fun schedulePeriodicSync() {
        WidgetRefreshWorker.schedule(context)
    }

    override fun cancelPeriodicSync() {
        WidgetRefreshWorker.cancel(context)
    }
}
