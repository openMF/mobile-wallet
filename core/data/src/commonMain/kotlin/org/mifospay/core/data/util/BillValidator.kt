/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.util

import kotlinx.datetime.Clock
import org.mifospay.core.model.autopay.Bill
import org.mifospay.core.model.autopay.BillFormData
import org.mifospay.core.model.autopay.BillValidationResult
import org.mifospay.core.model.autopay.RecurrencePattern

/**
 * Utility class for validating bill data before submission
 */
object BillValidator {

    /**
     * Validates a Bill object
     */
    fun validateBill(bill: Bill): BillValidationResult {
        val nameError = validateName(bill.name)
        val amountError = validateAmount(bill.amount)
        val dueDateError = validateDueDate(bill.dueDate)
        val recurrencePatternError = validateRecurrencePattern(bill.recurrencePattern)
        val billerError = validateBiller(bill.billerId, bill.billerName)

        val isValid = nameError == null &&
            amountError == null &&
            dueDateError == null &&
            recurrencePatternError == null &&
            billerError == null

        return BillValidationResult(
            isValid = isValid,
            nameError = nameError,
            amountError = amountError,
            dueDateError = dueDateError,
            recurrencePatternError = recurrencePatternError,
            billerError = billerError,
        )
    }

    /**
     * Validates BillFormData object
     */
    fun validateBillFormData(formData: BillFormData): BillValidationResult {
        val nameError = validateName(formData.name)
        val amountError = validateAmountString(formData.amount)
        val dueDateError = validateDueDate(formData.dueDate)
        val recurrencePatternError = validateRecurrencePattern(formData.recurrencePattern)
        val billerError = validateBiller(formData.billerId, formData.billerName)

        val isValid = nameError == null &&
            amountError == null &&
            dueDateError == null &&
            recurrencePatternError == null &&
            billerError == null

        return BillValidationResult(
            isValid = isValid,
            nameError = nameError,
            amountError = amountError,
            dueDateError = dueDateError,
            recurrencePatternError = recurrencePatternError,
            billerError = billerError,
        )
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Bill name is required"
            name.length < 2 -> "Bill name must be at least 2 characters long"
            name.length > 100 -> "Bill name must be less than 100 characters"
            !name.matches(Regex("^[a-zA-Z0-9\\s\\-_]+$")) -> "Bill name contains invalid characters"
            else -> null
        }
    }

    private fun validateAmount(amount: Double): String? {
        return when {
            amount <= 0 -> "Bill amount must be greater than 0"
            amount > 999999.99 -> "Bill amount cannot exceed 999,999.99"
            else -> null
        }
    }

    private fun validateAmountString(amountString: String): String? {
        return when {
            amountString.isBlank() -> "Bill amount is required"
            else -> {
                try {
                    val amount = amountString.toDouble()
                    validateAmount(amount)
                } catch (e: NumberFormatException) {
                    "Invalid amount format"
                }
            }
        }
    }

    private fun validateDueDate(dueDate: Long): String? {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return when {
            dueDate <= 0 -> "Due date is required"
            dueDate < currentTime -> "Due date cannot be in the past"
            dueDate > currentTime + (365 * 24 * 60 * 60 * 1000L) -> "Due date cannot be more than 1 year in the future"
            else -> null
        }
    }

    private fun validateRecurrencePattern(pattern: RecurrencePattern): String? {
        return when (pattern) {
            RecurrencePattern.NONE -> null
            RecurrencePattern.WEEKLY -> null
            RecurrencePattern.BIWEEKLY -> null
            RecurrencePattern.MONTHLY -> null
            RecurrencePattern.QUARTERLY -> null
            RecurrencePattern.SEMI_ANNUALLY -> null
            RecurrencePattern.ANNUALLY -> null
        }
    }

    private fun validateBiller(billerId: String?, billerName: String?): String? {
        return when {
            billerId.isNullOrBlank() && billerName.isNullOrBlank() -> null
            billerId.isNullOrBlank() && !billerName.isNullOrBlank() -> "Biller ID is required when biller name is provided"
            !billerId.isNullOrBlank() && billerName.isNullOrBlank() -> "Biller name is required when biller ID is provided"
            else -> null
        }
    }

    /**
     * Validates if a bill is overdue
     */
    fun isBillOverdue(bill: Bill): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return bill.dueDate < currentTime && bill.status == org.mifospay.core.model.autopay.BillStatus.ACTIVE
    }

    /**
     * Calculates the next payment date based on recurrence pattern
     */
    fun calculateNextPaymentDate(bill: Bill): Long {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return when (bill.recurrencePattern) {
            RecurrencePattern.NONE -> bill.dueDate
            RecurrencePattern.WEEKLY -> bill.dueDate + (7 * 24 * 60 * 60 * 1000L)
            RecurrencePattern.BIWEEKLY -> bill.dueDate + (14 * 24 * 60 * 60 * 1000L)
            RecurrencePattern.MONTHLY -> bill.dueDate + (30 * 24 * 60 * 60 * 1000L)
            RecurrencePattern.QUARTERLY -> bill.dueDate + (90 * 24 * 60 * 60 * 1000L)
            RecurrencePattern.SEMI_ANNUALLY -> bill.dueDate + (180 * 24 * 60 * 60 * 1000L)
            RecurrencePattern.ANNUALLY -> bill.dueDate + (365 * 24 * 60 * 60 * 1000L)
        }
    }

    /**
     * Checks if a bill is due within the specified number of days
     */
    fun isBillDueWithinDays(bill: Bill, days: Int): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val daysInMillis = days * 24 * 60 * 60 * 1000L
        return bill.dueDate <= currentTime + daysInMillis &&
            bill.dueDate > currentTime &&
            bill.status == org.mifospay.core.model.autopay.BillStatus.ACTIVE
    }
}
