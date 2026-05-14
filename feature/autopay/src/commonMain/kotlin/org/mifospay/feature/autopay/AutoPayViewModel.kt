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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.ui.utils.BaseViewModel

class AutoPayViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AutoPayState, AutoPayEvent, AutoPayAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: AutoPayState(),
) {

    companion object {
        private const val KEY_STATE = "autopay_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)

        loadDashboardData()
    }

    override fun handleAction(action: AutoPayAction) {
        when (action) {
            is AutoPayAction.SetupRecurringPayment -> {
                setupRecurringPayment()
            }
            is AutoPayAction.ConfigurePaymentRules -> {
                configurePaymentRules()
            }
            is AutoPayAction.ManagePaymentPreferences -> {
                managePaymentPreferences()
            }
            is AutoPayAction.ToggleAutoPay -> {
                toggleAutoPay(action.enabled)
            }
            is AutoPayAction.GetPaymentHistory -> {
                getPaymentHistory()
            }
            is AutoPayAction.RefreshDashboard -> {
                refreshDashboard()
            }
            is AutoPayAction.AddNewSchedule -> {
                addNewSchedule()
            }
            is AutoPayAction.ManageExistingSchedules -> {
                manageExistingSchedules()
            }
            is AutoPayAction.ViewScheduleDetails -> {
                viewScheduleDetails(action.scheduleId)
            }
            is AutoPayAction.AddNewBiller -> {
                addNewBiller()
            }
            is AutoPayAction.ViewBillerList -> {
                viewBillerList()
            }
            is AutoPayAction.AddNewBill -> {
                addNewBill()
            }
            is AutoPayAction.ViewBillList -> {
                viewBillList()
            }
        }
    }

    private fun loadDashboardData() {
        mutableStateFlow.update { it.copy(isLoading = true) }

        // Simulate API call delay
        viewModelScope.launch {
            delay(1000)

            val dummySchedules = listOf(
                AutoPaySchedule(
                    id = "1",
                    name = "Monthly Rent Payment",
                    amount = 1200.0,
                    currency = "USD",
                    frequency = "Monthly",
                    nextPaymentDate = "2024-02-15",
                    status = AutoPayStatus.ACTIVE,
                    recipientName = "Landlord Corp",
                    accountNumber = "****1234",
                ),
                AutoPaySchedule(
                    id = "2",
                    name = "Internet Bill",
                    amount = 89.99,
                    currency = "USD",
                    frequency = "Monthly",
                    nextPaymentDate = "2024-02-20",
                    status = AutoPayStatus.ACTIVE,
                    recipientName = "NetConnect",
                    accountNumber = "****5678",
                ),
                AutoPaySchedule(
                    id = "3",
                    name = "Gym Membership",
                    amount = 45.0,
                    currency = "USD",
                    frequency = "Monthly",
                    nextPaymentDate = "2024-02-25",
                    status = AutoPayStatus.PAUSED,
                    recipientName = "FitLife Gym",
                    accountNumber = "****9012",
                ),
            )

            val dummyUpcomingPayments = listOf(
                UpcomingPayment(
                    id = "1",
                    scheduleName = "Monthly Rent Payment",
                    amount = 1200.0,
                    currency = "USD",
                    dueDate = "2024-02-15",
                    status = PaymentStatus.UPCOMING,
                    recipientName = "Landlord Corp",
                ),
                UpcomingPayment(
                    id = "2",
                    scheduleName = "Internet Bill",
                    amount = 89.99,
                    currency = "USD",
                    dueDate = "2024-02-20",
                    status = PaymentStatus.UPCOMING,
                    recipientName = "NetConnect",
                ),
            )

            mutableStateFlow.update {
                it.copy(
                    isLoading = false,
                    activeSchedules = dummySchedules,
                    upcomingPayments = dummyUpcomingPayments,
                    totalActiveSchedules = dummySchedules.size,
                    totalUpcomingPayments = dummyUpcomingPayments.size,
                )
            }
        }
    }

    private fun refreshDashboard() {
        loadDashboardData()
    }

    private fun addNewSchedule() {
        sendEvent(AutoPayEvent.NavigateToSetup)
    }

    private fun manageExistingSchedules() {
        sendEvent(AutoPayEvent.NavigateToRules)
    }

    private fun viewScheduleDetails(scheduleId: String) {
        sendEvent(AutoPayEvent.NavigateToScheduleDetails(scheduleId))
    }

    private fun setupRecurringPayment() {
        sendEvent(AutoPayEvent.NavigateToSetup)
    }

    private fun configurePaymentRules() {
        sendEvent(AutoPayEvent.NavigateToRules)
    }

    private fun managePaymentPreferences() {
        sendEvent(AutoPayEvent.NavigateToPreferences)
    }

    private fun toggleAutoPay(enabled: Boolean) {
        mutableStateFlow.update {
            it.copy(isAutoPayEnabled = enabled)
        }
    }

    private fun getPaymentHistory() {
        sendEvent(AutoPayEvent.NavigateToHistory)
    }

    private fun addNewBiller() {
        sendEvent(AutoPayEvent.NavigateToAddBiller)
    }

    private fun viewBillerList() {
        sendEvent(AutoPayEvent.NavigateToBillerList)
    }

    private fun addNewBill() {
        sendEvent(AutoPayEvent.NavigateToAddBill)
    }

    private fun viewBillList() {
        sendEvent(AutoPayEvent.NavigateToBillList)
    }
}

@Serializable
data class AutoPayState(
    val isAutoPayEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeSchedules: List<AutoPaySchedule> = emptyList(),
    val upcomingPayments: List<UpcomingPayment> = emptyList(),
    val totalActiveSchedules: Int = 0,
    val totalUpcomingPayments: Int = 0,
)

@Serializable
data class AutoPaySchedule(
    val id: String,
    val name: String,
    val amount: Double,
    val currency: String,
    val frequency: String,
    val nextPaymentDate: String,
    val status: AutoPayStatus,
    val recipientName: String,
    val accountNumber: String,
)

@Serializable
data class UpcomingPayment(
    val id: String,
    val scheduleName: String,
    val amount: Double,
    val currency: String,
    val dueDate: String,
    val status: PaymentStatus,
    val recipientName: String,
)

@Serializable
enum class AutoPayStatus {
    ACTIVE,
    PAUSED,
    CANCELLED,
    COMPLETED,
}

@Serializable
enum class PaymentStatus {
    UPCOMING,
    PROCESSING,
    COMPLETED,
    FAILED,
}

sealed interface AutoPayEvent {
    data object NavigateToSetup : AutoPayEvent
    data object NavigateToRules : AutoPayEvent
    data object NavigateToPreferences : AutoPayEvent
    data object NavigateToHistory : AutoPayEvent
    data object NavigateToAddBiller : AutoPayEvent
    data object NavigateToBillerList : AutoPayEvent
    data object NavigateToAddBill : AutoPayEvent
    data object NavigateToBillList : AutoPayEvent
    data class NavigateToScheduleDetails(val scheduleId: String) : AutoPayEvent
}

sealed interface AutoPayAction {
    data object SetupRecurringPayment : AutoPayAction
    data object ConfigurePaymentRules : AutoPayAction
    data object ManagePaymentPreferences : AutoPayAction
    data object GetPaymentHistory : AutoPayAction
    data class ToggleAutoPay(val enabled: Boolean) : AutoPayAction
    data object RefreshDashboard : AutoPayAction
    data object AddNewSchedule : AutoPayAction
    data object ManageExistingSchedules : AutoPayAction
    data object AddNewBiller : AutoPayAction
    data object ViewBillerList : AutoPayAction
    data object AddNewBill : AutoPayAction
    data object ViewBillList : AutoPayAction
    data class ViewScheduleDetails(val scheduleId: String) : AutoPayAction
}
