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
import org.mifospay.core.model.autopay.AutoPayPayload
import org.mifospay.core.model.autopay.FrequencyOption
import org.mifospay.core.model.autopay.PaymentMethod
import org.mifospay.core.ui.utils.BaseViewModel

class AutoPaySetupViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AutoPaySetupState, AutoPaySetupEvent, AutoPaySetupAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: AutoPaySetupState(),
) {

    companion object {
        private const val KEY_STATE = "autopay_setup_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)

        loadInitialData()
    }

    override fun handleAction(action: AutoPaySetupAction) {
        when (action) {
            is AutoPaySetupAction.UpdatePaymentMethod -> {
                updatePaymentMethod(action.method)
            }
            is AutoPaySetupAction.UpdateFrequency -> {
                updateFrequency(action.frequency)
            }
            is AutoPaySetupAction.UpdateStartDate -> {
                updateStartDate(action.date)
            }
            is AutoPaySetupAction.UpdateEndDate -> {
                updateEndDate(action.date)
            }
            is AutoPaySetupAction.UpdateAmount -> {
                updateAmount(action.amount)
            }
            is AutoPaySetupAction.UpdateTermsAccepted -> {
                updateTermsAccepted(action.accepted)
            }
            is AutoPaySetupAction.NextStep -> {
                nextStep()
            }
            is AutoPaySetupAction.PreviousStep -> {
                previousStep()
            }
            is AutoPaySetupAction.ActivateAutoPay -> {
                activateAutoPay()
            }
            is AutoPaySetupAction.ValidateCurrentStep -> {
                validateCurrentStep()
            }
        }
    }

    private fun loadInitialData() {
        mutableStateFlow.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            delay(500)

            val paymentMethods = listOf(
                PaymentMethod(1, "BANK_ACCOUNT", "Bank Account", "Direct bank transfer"),
                PaymentMethod(2, "CARD", "Credit/Debit Card", "Card payment"),
                PaymentMethod(3, "UPI", "UPI", "Unified Payment Interface"),
            )

            val frequencyOptions = listOf(
                FrequencyOption(1, "DAILY", "Daily"),
                FrequencyOption(2, "WEEKLY", "Weekly"),
                FrequencyOption(3, "MONTHLY", "Monthly"),
                FrequencyOption(4, "QUARTERLY", "Quarterly"),
                FrequencyOption(5, "YEARLY", "Yearly"),
            )

            mutableStateFlow.update {
                it.copy(
                    isLoading = false,
                    paymentMethods = paymentMethods,
                    frequencyOptions = frequencyOptions,
                )
            }
        }
    }

    private fun updatePaymentMethod(method: PaymentMethod) {
        mutableStateFlow.update {
            it.copy(
                setupData = it.setupData.copy(paymentMethod = method),
            )
        }
    }

    private fun updateFrequency(frequency: FrequencyOption) {
        mutableStateFlow.update {
            it.copy(
                setupData = it.setupData.copy(frequency = frequency),
            )
        }
    }

    private fun updateStartDate(date: Long) {
        mutableStateFlow.update {
            it.copy(
                setupData = it.setupData.copy(startDate = date),
            )
        }
    }

    private fun updateEndDate(date: Long) {
        mutableStateFlow.update {
            it.copy(
                setupData = it.setupData.copy(endDate = date),
            )
        }
    }

    private fun updateAmount(amount: String) {
        mutableStateFlow.update {
            it.copy(
                setupData = it.setupData.copy(amount = amount),
            )
        }
    }

    private fun updateTermsAccepted(accepted: Boolean) {
        mutableStateFlow.update {
            it.copy(
                setupData = it.setupData.copy(termsAccepted = accepted),
            )
        }
    }

    private fun nextStep() {
        val currentState = stateFlow.value
        val currentStep = currentState.currentStep

        if (isStepValid(currentStep, currentState.setupData)) {
            val nextStep = when (currentStep) {
                SetupStep.PAYMENT_METHOD -> SetupStep.SCHEDULE_CONFIG
                SetupStep.SCHEDULE_CONFIG -> SetupStep.AMOUNT_CONFIRMATION
                SetupStep.AMOUNT_CONFIRMATION -> SetupStep.TERMS_ACCEPTANCE
                SetupStep.TERMS_ACCEPTANCE -> SetupStep.TERMS_ACCEPTANCE
            }

            mutableStateFlow.update {
                it.copy(currentStep = nextStep)
            }
        }
    }

    private fun previousStep() {
        val currentState = stateFlow.value
        val currentStep = currentState.currentStep

        val previousStep = when (currentStep) {
            SetupStep.PAYMENT_METHOD -> SetupStep.PAYMENT_METHOD
            SetupStep.SCHEDULE_CONFIG -> SetupStep.PAYMENT_METHOD
            SetupStep.AMOUNT_CONFIRMATION -> SetupStep.SCHEDULE_CONFIG
            SetupStep.TERMS_ACCEPTANCE -> SetupStep.AMOUNT_CONFIRMATION
        }

        mutableStateFlow.update {
            it.copy(currentStep = previousStep)
        }
    }

    private fun activateAutoPay() {
        val currentState = stateFlow.value
        val setupData = currentState.setupData

        if (isSetupComplete(setupData)) {
            mutableStateFlow.update { it.copy(isActivating = true) }

            viewModelScope.launch {
                delay(2000)

                val payload = AutoPayPayload(
                    name = "AutoPay Schedule",
                    description = "Automatic payment schedule",
                    amount = setupData.amount,
                    currency = "USD",
                    frequency = setupData.frequency?.code ?: "",
                    frequencyInterval = "1",
                    validFrom = setupData.startDate?.toString() ?: "",
                    validTill = setupData.endDate?.toString() ?: "",
                    paymentMethod = setupData.paymentMethod?.code ?: "",
                )

                sendEvent(AutoPaySetupEvent.AutoPayActivated(payload))
                mutableStateFlow.update { it.copy(isActivating = false) }
            }
        }
    }

    private fun validateCurrentStep() {
        val currentState = stateFlow.value
        val currentStep = currentState.currentStep
        val setupData = currentState.setupData

        val isValid = isStepValid(currentStep, setupData)
        mutableStateFlow.update {
            it.copy(isCurrentStepValid = isValid)
        }
    }

    private fun isStepValid(step: SetupStep, setupData: AutoPaySetupData): Boolean {
        return when (step) {
            SetupStep.PAYMENT_METHOD -> setupData.paymentMethod != null
            SetupStep.SCHEDULE_CONFIG -> setupData.frequency != null && setupData.startDate != null
            SetupStep.AMOUNT_CONFIRMATION -> setupData.amount.isNotEmpty() && setupData.amount.toDoubleOrNull() != null
            SetupStep.TERMS_ACCEPTANCE -> setupData.termsAccepted
        }
    }

    private fun isSetupComplete(setupData: AutoPaySetupData): Boolean {
        return setupData.paymentMethod != null &&
            setupData.frequency != null &&
            setupData.startDate != null &&
            setupData.amount.isNotEmpty() &&
            setupData.amount.toDoubleOrNull() != null &&
            setupData.termsAccepted
    }
}

@Serializable
data class AutoPaySetupState(
    val currentStep: SetupStep = SetupStep.PAYMENT_METHOD,
    val setupData: AutoPaySetupData = AutoPaySetupData(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val frequencyOptions: List<FrequencyOption> = emptyList(),
    val isLoading: Boolean = false,
    val isActivating: Boolean = false,
    val isCurrentStepValid: Boolean = false,
    val error: String? = null,
)

@Serializable
data class AutoPaySetupData(
    val paymentMethod: PaymentMethod? = null,
    val frequency: FrequencyOption? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val amount: String = "",
    val termsAccepted: Boolean = false,
)

@Serializable
enum class SetupStep {
    PAYMENT_METHOD,
    SCHEDULE_CONFIG,
    AMOUNT_CONFIRMATION,
    TERMS_ACCEPTANCE,
}

sealed interface AutoPaySetupEvent {
    data class AutoPayActivated(val payload: AutoPayPayload) : AutoPaySetupEvent
    data class NavigateToSuccess(val scheduleId: String) : AutoPaySetupEvent
    data class ShowError(val message: String) : AutoPaySetupEvent
}

sealed interface AutoPaySetupAction {
    data class UpdatePaymentMethod(val method: PaymentMethod) : AutoPaySetupAction
    data class UpdateFrequency(val frequency: FrequencyOption) : AutoPaySetupAction
    data class UpdateStartDate(val date: Long) : AutoPaySetupAction
    data class UpdateEndDate(val date: Long) : AutoPaySetupAction
    data class UpdateAmount(val amount: String) : AutoPaySetupAction
    data class UpdateTermsAccepted(val accepted: Boolean) : AutoPaySetupAction
    data object NextStep : AutoPaySetupAction
    data object PreviousStep : AutoPaySetupAction
    data object ActivateAutoPay : AutoPaySetupAction
    data object ValidateCurrentStep : AutoPaySetupAction
}
