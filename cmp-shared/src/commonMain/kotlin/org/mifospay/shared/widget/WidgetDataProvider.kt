package org.mifospay.shared.widget

import kotlinx.coroutines.flow.Flow

interface WidgetDataProvider {

    /** Emits a fresh [WidgetState] whenever auth state or balance changes. */
    val widgetStateFlow: Flow<WidgetState>

    /** Call after a transaction to force a balance re-fetch. */
    fun invalidate()
}
