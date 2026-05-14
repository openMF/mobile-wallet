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

import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import org.mifospay.core.ui.utils.BaseViewModel

class AutoPayViewModel : BaseViewModel<AutoPayState, AutoPayEvent, AutoPayAction>(
    initialState = AutoPayState(),
) {

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
        }
    }

    private fun setupRecurringPayment() {
        // TODO: Implement recurring payment setup logic
        sendEvent(AutoPayEvent.NavigateToSetup)
    }

    private fun configurePaymentRules() {
        // TODO: Implement payment rules configuration
        sendEvent(AutoPayEvent.NavigateToRules)
    }

    private fun managePaymentPreferences() {
        // TODO: Implement payment preferences management
        sendEvent(AutoPayEvent.NavigateToPreferences)
    }

    private fun toggleAutoPay(enabled: Boolean) {
        // TODO: Implement auto-pay toggle logic
        mutableStateFlow.update {
            it.copy(isAutoPayEnabled = enabled)
        }
    }

    private fun getPaymentHistory() {
        // TODO: Implement payment history retrieval
        sendEvent(AutoPayEvent.NavigateToHistory)
    }
}

@Serializable
data class AutoPayState(
    val isAutoPayEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface AutoPayEvent {
    data object NavigateToSetup : AutoPayEvent
    data object NavigateToRules : AutoPayEvent
    data object NavigateToPreferences : AutoPayEvent
    data object NavigateToHistory : AutoPayEvent
}

sealed interface AutoPayAction {
    data object SetupRecurringPayment : AutoPayAction
    data object ConfigurePaymentRules : AutoPayAction
    data object ManagePaymentPreferences : AutoPayAction
    data object GetPaymentHistory : AutoPayAction
    data class ToggleAutoPay(val enabled: Boolean) : AutoPayAction
}
