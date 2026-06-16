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