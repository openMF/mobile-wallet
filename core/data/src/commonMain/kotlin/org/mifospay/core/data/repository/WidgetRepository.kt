package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.widget.WidgetData
/**
 * Repository interface for widget data.
 *
 * Lives in :core:data alongside other repository interfaces
 * (e.g. AccountRepository, ClientRepository).
 *
 * Callers depend on this interface — never on the implementation.
 * Koin provides the concrete binding via [WidgetRepositoryImpl].
 */
interface WidgetRepository {

    /** Hot flow of the current widget state. Emits on every [saveWidgetData] call. */
    val widgetDataStream: Flow<WidgetData>

    /** Returns the latest persisted [WidgetData], or [WidgetData.DEFAULT] if none. */
    suspend fun getWidgetData(): WidgetData

    /** Persist [data] and push it downstream via [widgetDataStream]. */
    suspend fun saveWidgetData(data: WidgetData)

    /** Reset widget storage to defaults (e.g. on logout). */
    suspend fun clearWidgetData()
}
