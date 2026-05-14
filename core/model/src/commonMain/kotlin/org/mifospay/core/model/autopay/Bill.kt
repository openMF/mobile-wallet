/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.autopay

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class Bill(
    val id: String? = null,
    val name: String,
    val amount: Double,
    val currency: String = "USD",
    val dueDate: Long,
    val recurrencePattern: RecurrencePattern,
    val billerId: String? = null,
    val billerName: String? = null,
    val description: String? = null,
    val isActive: Boolean = true,
    val status: BillStatus = BillStatus.ACTIVE,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
) : Parcelable

@Serializable
enum class RecurrencePattern(val displayName: String, val interval: Int) {
    NONE("No Recurrence", 0),
    WEEKLY("Weekly", 7),
    BIWEEKLY("Bi-weekly", 14),
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 90),
    SEMI_ANNUALLY("Semi-annually", 180),
    ANNUALLY("Annually", 365),
    ;

    companion object {
        fun fromDisplayName(displayName: String): RecurrencePattern? {
            return entries.find { it.displayName == displayName }
        }
    }
}

@Serializable
enum class BillStatus {
    ACTIVE,
    PAUSED,
    CANCELLED,
    COMPLETED,
}

@Serializable
data class BillFormData(
    val name: String = "",
    val amount: String = "",
    val currency: String = "USD",
    val dueDate: Long = 0L,
    val recurrencePattern: RecurrencePattern = RecurrencePattern.NONE,
    val billerId: String? = null,
    val billerName: String? = null,
    val description: String = "",
)

@Serializable
data class BillValidationResult(
    val isValid: Boolean,
    val nameError: String? = null,
    val amountError: String? = null,
    val dueDateError: String? = null,
    val recurrencePatternError: String? = null,
    val billerError: String? = null,
)

@Serializable
data class NextPaymentDate(
    val date: Long,
    val formattedDate: String,
    val isOverdue: Boolean = false,
)
