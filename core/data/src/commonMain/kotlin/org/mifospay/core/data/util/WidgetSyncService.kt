package org.mifospay.core.data.util

import org.mifospay.core.model.widget.WidgetData

/**
 * Platform capability: push data to the home screen widget and manage
 * background refresh scheduling.
 *
 * Lives in :core:data — depends on nothing platform-specific.
 * Android actual → [AndroidWidgetSyncService] in :cmp-android
 * iOS actual     → IosWidgetSyncService in :cmp-ios (future)
 *
 * Use cases depend on this interface, never on Glance or WidgetKit.
 * This is the Dependency Inversion boundary.
 */
interface WidgetSyncService {

    /** Tell the home screen to re-render all placed widget instances with [data]. */
    suspend fun syncToWidget(data: WidgetData)

    /**
     * Re-render all placed widget instances from whatever is already in storage.
     * Does not touch storage — just triggers a Glance / WidgetKit redraw.
     */
    suspend fun refreshWidget()

    /** Register a periodic background refresh (WorkManager / WidgetKit timeline). */
    fun schedulePeriodicSync()

    /** Cancel periodic background refresh (call when last widget instance is removed). */
    fun cancelPeriodicSync()
}