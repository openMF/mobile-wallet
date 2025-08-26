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
import org.mifospay.core.model.autopay.BillFormData
import org.mifospay.core.model.autopay.BillValidationResult
import org.mifospay.core.model.autopay.RecurrencePattern

object BillValidator {

    fun validateNameField(name: String): String? {
        return when {
            name.isBlank() -> "Bill name is required"
            name.length < 3 -> "Bill name must be at least 3 characters"
            name.length > 100 -> "Bill name must be less than 100 characters"
            else -> null
        }
    }

    fun validateAmountField(amount: String): String? {
        return when {
            amount.isBlank() -> "Amount is required"
            !amount.matches(Regex("^\\d+(\\.\\d{1,2})?$")) -> "Please enter a valid amount"
            amount.toDoubleOrNull() == null -> "Please enter a valid number"
            amount.toDoubleOrNull()!! <= 0 -> "Amount must be greater than 0"
            amount.toDoubleOrNull()!! > 999999.99 -> "Amount cannot exceed 999,999.99"
            else -> null
        }
    }

    fun validateDueDateField(dueDate: Long): String? {
        return when {
            dueDate == 0L -> "Due date is required"
            dueDate < Clock.System.now().toEpochMilliseconds() -> "Due date cannot be in the past"
            else -> null
        }
    }

    fun validateRecurrencePatternField(recurrencePattern: RecurrencePattern): String? {
        return when {
            recurrencePattern == RecurrencePattern.NONE -> null
            else -> null
        }
    }

    fun validateBillerField(billerId: String?, billerName: String?): String? {
        return when {
            billerId.isNullOrBlank() || billerName.isNullOrBlank() -> "Please select a biller"
            else -> null
        }
    }

    fun validateBillForm(formData: BillFormData): BillValidationResult {
        val nameError = validateNameField(formData.name)
        val amountError = validateAmountField(formData.amount)
        val dueDateError = validateDueDateField(formData.dueDate)
        val recurrencePatternError = validateRecurrencePatternField(formData.recurrencePattern)
        val billerError = validateBillerField(formData.billerId, formData.billerName)

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
}
