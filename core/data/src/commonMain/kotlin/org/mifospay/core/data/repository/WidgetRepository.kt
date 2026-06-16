/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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
