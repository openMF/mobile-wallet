/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.settings.generated.resources.Res
import mobile_wallet.feature.settings.generated.resources.feature_settings_alert_disable_account
import mobile_wallet.feature.settings.generated.resources.feature_settings_alert_disable_account_desc
import mobile_wallet.feature.settings.generated.resources.feature_settings_empty
import mobile_wallet.feature.settings.generated.resources.feature_settings_log_out_title
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.client.Client
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.settings.SettingsAction.Internal.DisableAccountResult

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val repository: SavingsAccountRepository,
) : BaseViewModel<SettingsState, SettingsEvent, SettingsAction>(
    initialState = run {
        val client = requireNotNull(userPreferencesRepository.client.value)

        SettingsState(
            client = client,
            dialogState = null,
        )
    },
) {

    override fun handleAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.NavigateBack -> {
                sendEvent(SettingsEvent.OnNavigateBack)
            }

            is SettingsAction.NavigateToFaqScreen -> {
                sendEvent(SettingsEvent.OnNavigateToFaqScreen)
            }

            is SettingsAction.ChangePasscode -> {
                sendEvent(SettingsEvent.OnNavigateToChangePasscodeScreen)
            }

            is SettingsAction.ChangePassword -> {
                sendEvent(SettingsEvent.OnNavigateToEditPasswordScreen)
            }

            is SettingsAction.NavigateToNotificationSettings -> {
                sendEvent(SettingsEvent.OnNavigateToNotificationScreen)
            }

            is SettingsAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is SettingsAction.DisableAccount -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = DialogState.DisableAccount(
                            title = Res.string.feature_settings_alert_disable_account,
                            message = Res.string.feature_settings_alert_disable_account_desc,
                            onConfirm = {
                                trySendAction(SettingsAction.Internal.DisableAccount)
                            },
                        ),
                    )
                }
            }

            is SettingsAction.Logout -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = DialogState.Logout(
                            title = Res.string.feature_settings_log_out_title,
                            message = Res.string.feature_settings_empty,
                            onConfirm = {
                                trySendAction(SettingsAction.DismissDialog)
                                sendEvent(SettingsEvent.OnNavigateToLogout)
                            },
                        ),
                    )
                }
            }

            is SettingsAction.Internal.DisableAccount -> handleDisableAccount()

            is DisableAccountResult -> handleDisableAccountResult(action)
        }
    }

    private fun handleDisableAccount() {
        mutableStateFlow.update {
            it.copy(dialogState = DialogState.Loading)
        }

        // TODO:: this shouldn't work, we need account id to block account
        viewModelScope.launch {
            val result = repository.blockAccount(state.client.id)
            sendAction(DisableAccountResult(result))
        }
    }

    private fun handleDisableAccountResult(action: DisableAccountResult) {
        when (action.result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = DialogState.Error(
                            action.result.exception.message ?: "Error",
                        ),
                    )
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = DialogState.Loading)
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                sendEvent(SettingsEvent.OnNavigateToLogout)
            }
        }
    }
}

data class SettingsState(
    val client: Client,
    val dialogState: DialogState? = null,
)

sealed interface DialogState {
    data object Loading : DialogState
    data class Error(val message: String) : DialogState
    data class DisableAccount(
        val title: StringResource,
        val message: StringResource,
        val onConfirm: () -> Unit,
    ) : DialogState

    data class Logout(
        val title: StringResource,
        val message: StringResource,
        val onConfirm: () -> Unit,
    ) : DialogState
}

sealed interface SettingsEvent {
    data object OnNavigateBack : SettingsEvent
    data object OnNavigateToEditPasswordScreen : SettingsEvent
    data object OnNavigateToChangePasscodeScreen : SettingsEvent
    data object OnNavigateToLogout : SettingsEvent
    data object OnNavigateToFaqScreen : SettingsEvent
    data object OnNavigateToNotificationScreen : SettingsEvent
}

sealed interface SettingsAction {
    data object NavigateBack : SettingsAction
    data object Logout : SettingsAction
    data object DisableAccount : SettingsAction
    data object ChangePasscode : SettingsAction
    data object ChangePassword : SettingsAction
    data object NavigateToFaqScreen : SettingsAction
    data object NavigateToNotificationSettings : SettingsAction
    data object DismissDialog : SettingsAction

    sealed interface Internal : SettingsAction {
        data object DisableAccount : Internal
        data class DisableAccountResult(val result: DataState<String>) : Internal
    }
}
