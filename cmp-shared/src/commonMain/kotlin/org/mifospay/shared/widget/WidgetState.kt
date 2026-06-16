package org.mifospay.shared.widget

import org.mifospay.core.model.widget.WidgetData

/**
 * Sealed result returned by [WidgetDataProvider.getWidgetState].
 *
 * [FinanceGlanceWidget] switches its entire UI based on this —
 * no auth logic lives in the Glance composable itself.
 */
sealed interface WidgetState {

    /** User is not logged in — widget shows a "Sign in" prompt. */
    data object Unauthenticated : WidgetState

    /** User is logged in — widget renders balance, budget, quick actions. */
    data class Authenticated(val data: WidgetData) : WidgetState

    /** Balance fetch failed — widget shows an error prompt. */
    data object Error : WidgetState
}