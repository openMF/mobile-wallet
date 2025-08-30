/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.autopay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.getSerialized
import org.mifospay.core.data.util.BillValidator
import org.mifospay.core.datastore.BillRepository
import org.mifospay.core.model.autopay.Bill
import org.mifospay.core.model.autopay.BillFormData
import org.mifospay.core.model.autopay.BillValidationResult
import org.mifospay.core.model.autopay.Biller
import org.mifospay.core.model.autopay.NextPaymentDate
import org.mifospay.core.model.autopay.RecurrencePattern
import org.mifospay.core.ui.utils.BaseViewModel

class EditBillViewModel(
    savedStateHandle: SavedStateHandle,
    private val billRepository: BillRepository,
    private val billerRepository: org.mifospay.core.datastore.BillerRepository,
) : BaseViewModel<EditBillState, EditBillEvent, EditBillAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: EditBillState(),
) {

    private val billId: String = savedStateHandle["billId"] ?: ""

    init {
        loadBill()
        loadAvailableBillers()
    }

    override fun handleAction(action: EditBillAction) {
        when (action) {
            is EditBillAction.UpdateBillName -> {
                updateBillName(action.name)
            }
            is EditBillAction.UpdateAmount -> {
                updateAmount(action.amount)
            }
            is EditBillAction.UpdateDueDate -> {
                updateDueDate(action.dueDate)
            }
            is EditBillAction.UpdateRecurrencePattern -> {
                updateRecurrencePattern(action.recurrencePattern)
            }
            is EditBillAction.UpdateDescription -> {
                updateDescription(action.description)
            }
            is EditBillAction.UpdateBill -> {
                updateBill()
            }
            is EditBillAction.ValidateForm -> {
                validateForm()
            }
            is EditBillAction.ClearError -> {
                clearError()
            }
            is EditBillAction.ClearValidationErrors -> {
                clearValidationErrors()
            }
            is EditBillAction.CalculateNextPaymentDates -> {
                calculateNextPaymentDates()
            }
            is EditBillAction.SelectBiller -> {
                selectBiller(action.biller)
            }
        }
    }

    private fun loadBill() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isLoading = true) }

            try {
                val bill = billRepository.getBillById(billId)
                if (bill != null) {
                    val formData = BillFormData(
                        name = bill.name,
                        amount = bill.amount.toString(),
                        currency = bill.currency,
                        dueDate = bill.dueDate,
                        recurrencePattern = bill.recurrencePattern,
                        billerId = bill.billerId,
                        billerName = bill.billerName,
                        description = bill.description ?: "",
                    )
                    mutableStateFlow.update {
                        it.copy(
                            formData = formData,
                            isLoading = false,
                            error = null,
                        )
                    }
                    calculateNextPaymentDates()
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            isLoading = false,
                            error = "Bill not found",
                        )
                    }
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load bill: ${e.message}",
                    )
                }
            }
        }
    }

    private fun updateBillName(name: String) {
        val nameError = BillValidator.validateBillFormData(
            BillFormData(name = name),
        ).nameError
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(name = name),
                validationResult = it.validationResult.copy(nameError = nameError),
            )
        }
    }

    private fun updateAmount(amount: String) {
        val amountError = BillValidator.validateBillFormData(
            BillFormData(amount = amount),
        ).amountError
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(amount = amount),
                validationResult = it.validationResult.copy(amountError = amountError),
            )
        }
    }

    private fun updateDueDate(dueDate: Long) {
        val dueDateError = BillValidator.validateBillFormData(
            BillFormData(dueDate = dueDate),
        ).dueDateError
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(dueDate = dueDate),
                validationResult = it.validationResult.copy(dueDateError = dueDateError),
            )
        }
        calculateNextPaymentDates()
    }

    private fun updateRecurrencePattern(recurrencePattern: RecurrencePattern) {
        val recurrencePatternError = BillValidator.validateBillFormData(
            BillFormData(recurrencePattern = recurrencePattern),
        ).recurrencePatternError
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(recurrencePattern = recurrencePattern),
                validationResult = it.validationResult.copy(recurrencePatternError = recurrencePatternError),
            )
        }
        calculateNextPaymentDates()
    }

    private fun updateDescription(description: String) {
        mutableStateFlow.update {
            it.copy(formData = it.formData.copy(description = description))
        }
    }

    private fun validateForm(): BillValidationResult {
        val currentState = stateFlow.value
        val formData = currentState.formData

        val validationResult = BillValidator.validateBillFormData(formData)

        mutableStateFlow.update { it.copy(validationResult = validationResult) }
        return validationResult
    }

    private fun calculateNextPaymentDates() {
        val currentState = stateFlow.value
        val formData = currentState.formData

        if (formData.dueDate == 0L || formData.recurrencePattern == RecurrencePattern.NONE) {
            mutableStateFlow.update { it.copy(nextPaymentDates = emptyList()) }
            return
        }

        val nextDates = mutableListOf<NextPaymentDate>()
        val currentTime = Clock.System.now().toEpochMilliseconds()
        var nextDate = formData.dueDate

        // Generate next 5 payment dates
        repeat(5) {
            if (nextDate >= currentTime) {
                val isOverdue = nextDate < currentTime
                val formattedDate = formatDate(nextDate)
                nextDates.add(NextPaymentDate(nextDate, formattedDate, isOverdue))
            }

            // Calculate next date based on recurrence pattern
            nextDate += (formData.recurrencePattern.interval * 24 * 60 * 60 * 1000L)
        }

        mutableStateFlow.update { it.copy(nextPaymentDates = nextDates) }
    }

    private fun formatDate(timestamp: Long): String {
        return DateHelper.getDateAsStringFromLong(timestamp)
    }

    private fun updateBill() {
        val validationResult = validateForm()

        if (!validationResult.isValid) {
            return
        }

        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isLoading = true) }

            try {
                val formData = mutableStateFlow.value.formData

                val bill = Bill(
                    id = billId,
                    name = formData.name.trim(),
                    amount = formData.amount.toDoubleOrNull() ?: 0.0,
                    currency = formData.currency,
                    dueDate = formData.dueDate,
                    recurrencePattern = formData.recurrencePattern,
                    billerId = formData.billerId,
                    billerName = formData.billerName,
                    description = formData.description.takeIf { it.isNotBlank() },
                )

                val result = billRepository.updateBill(bill)

                when (result) {
                    is DataState.Loading -> {
                        // Loading state is already handled by setting isLoading = true above
                    }
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                            )
                        }
                        sendEvent(EditBillEvent.BillUpdated(result.data))
                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to update bill: ${result.exception.message}",
                            )
                        }
                    }
                }
            } catch (exception: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to update bill: ${exception.message}",
                    )
                }
            }
        }
    }

    private fun loadAvailableBillers() {
        viewModelScope.launch {
            try {
                billerRepository.getAllBillers().collect { billers ->
                    mutableStateFlow.update { it.copy(availableBillers = billers) }
                }
            } catch (exception: Exception) {
                // Handle error silently for now
            }
        }
    }

    private fun selectBiller(biller: Biller) {
        val billerError = BillValidator.validateBillFormData(
            BillFormData(billerId = biller.id, billerName = biller.name),
        ).billerError
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(
                    billerId = biller.id,
                    billerName = biller.name,
                ),
                validationResult = it.validationResult.copy(billerError = billerError),
            )
        }
    }

    private fun clearError() {
        mutableStateFlow.update { it.copy(error = null) }
    }

    private fun clearValidationErrors() {
        mutableStateFlow.update {
            it.copy(
                validationResult = BillValidationResult(isValid = false),
            )
        }
    }

    companion object {
        private const val KEY_STATE = "edit_bill_state"
    }
}

@Serializable
data class EditBillState(
    val formData: BillFormData = BillFormData(),
    val validationResult: BillValidationResult = BillValidationResult(isValid = false),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val nextPaymentDates: List<NextPaymentDate> = emptyList(),
    val availableBillers: List<Biller> = emptyList(),
)

sealed interface EditBillEvent {
    data class BillUpdated(val bill: Bill) : EditBillEvent
}

sealed interface EditBillAction {
    data class UpdateBillName(val name: String) : EditBillAction
    data class UpdateAmount(val amount: String) : EditBillAction
    data class UpdateDueDate(val dueDate: Long) : EditBillAction
    data class UpdateRecurrencePattern(val recurrencePattern: RecurrencePattern) : EditBillAction
    data class UpdateDescription(val description: String) : EditBillAction
    data object UpdateBill : EditBillAction
    data object ValidateForm : EditBillAction
    data object ClearError : EditBillAction
    data object ClearValidationErrors : EditBillAction
    data object CalculateNextPaymentDates : EditBillAction
    data class SelectBiller(val biller: Biller) : EditBillAction
}
