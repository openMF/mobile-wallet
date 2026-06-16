/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.widget

import kotlinx.serialization.Serializable

/**
 * Canonical widget state model.
 *
 * Lives in :core:model so every module that needs to read or write widget
 * data can depend on it without pulling in persistence or UI code.
 *
 * Mirrors the pattern of [org.mifospay.core.model.user.UserInfo] —
 * pure data, fully serializable, no platform dependencies.
 */
@Serializable
data class WidgetData(
    val currentBalance: Double = 0.0,
    val currency: String = "USD",
    val accountNumber: String = "",
    val budgetTotal: Double = 0.0,
    val budgetSpent: Double = 0.0,
    val lastIncome: Double = 0.0,
    val lastExpense: Double = 0.0,
    val lastUpdatedMs: Long = 0L,
    val userName: String = "",
) {
    val budgetRemaining: Double
        get() = (budgetTotal - budgetSpent).coerceAtLeast(0.0)

    val budgetUsedFraction: Float
        get() = if (budgetTotal <= 0.0) {
            0f
        } else {
            (budgetSpent / budgetTotal).toFloat().coerceIn(0f, 1f)
        }

    val isBudgetCritical: Boolean get() = budgetUsedFraction >= 0.85f
    val isBudgetWarning: Boolean get() = budgetUsedFraction >= 0.65f
    val hasBudget: Boolean get() = budgetTotal > 0.0

    companion object {
        val DEFAULT = WidgetData()
    }
}
