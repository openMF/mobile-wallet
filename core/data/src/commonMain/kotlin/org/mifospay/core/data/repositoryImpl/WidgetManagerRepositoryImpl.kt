/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import org.mifospay.core.data.repository.WidgetManagerRepository
import org.mifospay.core.data.repository.WidgetRepository
import org.mifospay.core.data.util.WidgetSyncService
import org.mifospay.core.model.widget.WidgetData

class WidgetManagerRepositoryImpl(
    private val repository: WidgetRepository,
    private val widgetSyncService: WidgetSyncService,
) : WidgetManagerRepository {

    override suspend fun refresh() {
        widgetSyncService.refreshWidget()
    }

    override suspend fun update(data: WidgetData) {
        repository.saveWidgetData(data)
        widgetSyncService.refreshWidget()
    }

    override suspend fun clear() {
        repository.clearWidgetData()
        widgetSyncService.refreshWidget()
    }

    override fun schedule() {
        widgetSyncService.schedulePeriodicSync()
    }

    override fun cancel() {
        widgetSyncService.cancelPeriodicSync()
    }
}
