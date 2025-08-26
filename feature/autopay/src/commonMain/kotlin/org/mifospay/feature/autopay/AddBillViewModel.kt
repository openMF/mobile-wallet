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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.util.BillValidator
import org.mifospay.core.model.autopay.Bill
import org.mifospay.core.model.autopay.BillFormData
import org.mifospay.core.model.autopay.BillValidationResult
import org.mifospay.core.model.autopay.Biller
import org.mifospay.core.model.autopay.NextPaymentDate
import org.mifospay.core.model.autopay.RecurrencePattern
import org.mifospay.core.ui.utils.BaseViewModel
import kotlin.random.Random

class AddBillViewModel(
    savedStateHandle: SavedStateHandle,
    private val billRepository: org.mifospay.core.datastore.BillRepository,
    private val billerRepository: org.mifospay.core.datastore.BillerRepository,
) : BaseViewModel<AddBillState, AddBillEvent, AddBillAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: AddBillState(),
) {

    companion object {
        private const val KEY_STATE = "add_bill_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)

        // Load available billers
        loadAvailableBillers()
    }

    override fun handleAction(action: AddBillAction) {
        when (action) {
            is AddBillAction.UpdateBillName -> {
                updateBillName(action.name)
            }
            is AddBillAction.UpdateAmount -> {
                updateAmount(action.amount)
            }
            is AddBillAction.UpdateDueDate -> {
                updateDueDate(action.dueDate)
            }
            is AddBillAction.UpdateRecurrencePattern -> {
                updateRecurrencePattern(action.recurrencePattern)
            }
            is AddBillAction.UpdateDescription -> {
                updateDescription(action.description)
            }
            is AddBillAction.SaveBill -> {
                saveBill()
            }
            is AddBillAction.ValidateForm -> {
                validateForm()
            }
            is AddBillAction.ClearError -> {
                clearError()
            }
            is AddBillAction.ClearValidationErrors -> {
                clearValidationErrors()
            }
            is AddBillAction.CalculateNextPaymentDates -> {
                calculateNextPaymentDates()
            }
            is AddBillAction.SelectBiller -> {
                selectBiller(action.biller)
            }
            is AddBillAction.RefreshBillers -> {
                loadAvailableBillers()
            }
        }
    }

    private fun updateBillName(name: String) {
        val nameError = BillValidator.validateNameField(name)
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(name = name),
                validationResult = it.validationResult.copy(nameError = nameError),
            )
        }
    }

    private fun updateAmount(amount: String) {
        val amountError = BillValidator.validateAmountField(amount)
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(amount = amount),
                validationResult = it.validationResult.copy(amountError = amountError),
            )
        }
    }

    private fun updateDueDate(dueDate: Long) {
        val dueDateError = BillValidator.validateDueDateField(dueDate)
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(dueDate = dueDate),
                validationResult = it.validationResult.copy(dueDateError = dueDateError),
            )
        }
        calculateNextPaymentDates()
    }

    private fun updateRecurrencePattern(recurrencePattern: RecurrencePattern) {
        val recurrencePatternError = BillValidator.validateRecurrencePatternField(recurrencePattern)
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

        val validationResult = BillValidator.validateBillForm(formData)

        mutableStateFlow.update { it.copy(validationResult = validationResult) }
        return validationResult
    }

    private fun generateUniqueId(): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val random = Random.nextInt(100000, 999999)
        return "$timestamp-$random"
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

    private fun saveBill() {
        val validationResult = validateForm()

        if (!validationResult.isValid) {
            return
        }

        mutableStateFlow.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val currentState = stateFlow.value
                val formData = currentState.formData

                val bill = Bill(
                    id = generateUniqueId(),
                    name = formData.name.trim(),
                    amount = formData.amount.toDoubleOrNull() ?: 0.0,
                    currency = formData.currency,
                    dueDate = formData.dueDate,
                    recurrencePattern = formData.recurrencePattern,
                    billerId = formData.billerId,
                    billerName = formData.billerName,
                    description = formData.description.takeIf { it.isNotBlank() },
                )

                val result = billRepository.saveBill(bill)

                when (result) {
                    is org.mifospay.core.common.DataState.Loading -> {
                        // Loading state is already handled by setting isLoading = true above
                    }
                    is org.mifospay.core.common.DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                            )
                        }
                        sendEvent(AddBillEvent.BillSaved(result.data))
                    }
                    is org.mifospay.core.common.DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to save bill: ${result.exception.message}",
                            )
                        }
                    }
                }
            } catch (exception: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save bill: ${exception.message}",
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
        val billerError = BillValidator.validateBillerField(biller.id, biller.name)
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
}

@Serializable
data class AddBillState(
    val formData: BillFormData = BillFormData(),
    val validationResult: BillValidationResult = BillValidationResult(isValid = false),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val nextPaymentDates: List<NextPaymentDate> = emptyList(),
    val availableBillers: List<Biller> = emptyList(),
)

sealed interface AddBillEvent {
    data class BillSaved(val bill: Bill) : AddBillEvent
}

sealed interface AddBillAction {
    data class UpdateBillName(val name: String) : AddBillAction
    data class UpdateAmount(val amount: String) : AddBillAction
    data class UpdateDueDate(val dueDate: Long) : AddBillAction
    data class UpdateRecurrencePattern(val recurrencePattern: RecurrencePattern) : AddBillAction
    data class UpdateDescription(val description: String) : AddBillAction
    data object SaveBill : AddBillAction
    data object ValidateForm : AddBillAction
    data object ClearError : AddBillAction
    data object ClearValidationErrors : AddBillAction
    data object CalculateNextPaymentDates : AddBillAction
    data class SelectBiller(val biller: Biller) : AddBillAction
    data object RefreshBillers : AddBillAction
}
