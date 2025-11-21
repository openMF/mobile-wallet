/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.interbank

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.network.model.entity.TPTResponse
import org.mifospay.core.network.model.entity.payload.TransferPayload
import org.mifospay.core.ui.utils.BaseViewModel

/**
 * ViewModel for managing interbank transfer flow
 * Handles all stages: account selection, recipient search, transfer details, preview, and confirmation
 */
class InterbankTransferViewModel(
    private val thirdPartyTransferRepository: ThirdPartyTransferRepository,
    private val selfServiceRepository: SelfServiceRepository,
    private val preferencesRepository: UserPreferencesRepository,
) : BaseViewModel<InterbankTransferState, InterbankTransferEvent, InterbankTransferAction>(
    initialState = run {
        val client = requireNotNull(preferencesRepository.client.value)
        InterbankTransferState(client = client)
    },
) {

    init {
        viewModelScope.launch {
            loadFromAccounts()
        }
    }

    override fun handleAction(action: InterbankTransferAction) {
        when (action) {
            // Navigation actions
            is InterbankTransferAction.NavigateToRecipientSearch -> {
                mutableStateFlow.update {
                    it.copy(
                        currentStep = InterbankTransferState.Step.SearchRecipient,
                        selectedFromAccount = action.account,
                    )
                }
            }

            is InterbankTransferAction.NavigateToTransferDetails -> {
                mutableStateFlow.update {
                    it.copy(
                        currentStep = InterbankTransferState.Step.TransferDetails,
                        selectedRecipient = action.recipient,
                    )
                }
            }

            is InterbankTransferAction.NavigateToPreview -> {
                mutableStateFlow.update {
                    it.copy(currentStep = InterbankTransferState.Step.PreviewTransfer)
                }
            }

            InterbankTransferAction.NavigateBack -> {
                val previousStep = when (state.currentStep) {
                    InterbankTransferState.Step.SelectAccount -> {
                        sendEvent(InterbankTransferEvent.OnNavigateBack)
                        return
                    }
                    InterbankTransferState.Step.SearchRecipient -> InterbankTransferState.Step.SelectAccount
                    InterbankTransferState.Step.TransferDetails -> InterbankTransferState.Step.SearchRecipient
                    InterbankTransferState.Step.PreviewTransfer -> InterbankTransferState.Step.TransferDetails
                    InterbankTransferState.Step.TransferSuccess -> InterbankTransferState.Step.PreviewTransfer
                    InterbankTransferState.Step.TransferFailed -> InterbankTransferState.Step.PreviewTransfer
                }
                mutableStateFlow.update {
                    it.copy(currentStep = previousStep)
                }
            }

            // Transfer details actions
            is InterbankTransferAction.UpdateAmount -> {
                mutableStateFlow.update {
                    it.copy(transferAmount = action.amount)
                }
            }

            is InterbankTransferAction.UpdateDate -> {
                mutableStateFlow.update {
                    it.copy(transferDate = action.date)
                }
            }

            is InterbankTransferAction.UpdateDescription -> {
                mutableStateFlow.update {
                    it.copy(transferDescription = action.description)
                }
            }

            // Transfer confirmation
            InterbankTransferAction.ConfirmTransfer -> {
                validateAndInitiateTransfer()
            }

            // Error handling
            InterbankTransferAction.RetryTransfer -> {
                mutableStateFlow.update {
                    it.copy(currentStep = InterbankTransferState.Step.PreviewTransfer)
                }
            }

            InterbankTransferAction.DismissError -> {
                mutableStateFlow.update {
                    it.copy(errorMessage = null)
                }
            }

            is InterbankTransferAction.Internal.HandleTransferResult -> {
                handleTransferResult(action)
            }
        }
    }

    private suspend fun loadFromAccounts() {
        try {
            
            mutableStateFlow.update {
                it.copy(loadingState = InterbankTransferState.LoadingState.Loading)
            }

            selfServiceRepository.getActiveAccounts(state.client.id).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                loadingState = InterbankTransferState.LoadingState.Error(
                                    result.message ?: "Failed to load accounts"
                                ),
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(loadingState = InterbankTransferState.LoadingState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val accounts = result.data
                        if (accounts.isEmpty()) {
                            mutableStateFlow.update {
                                it.copy(
                                    loadingState = InterbankTransferState.LoadingState.Error(
                                        "No accounts available"
                                    ),
                                )
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(
                                    loadingState = InterbankTransferState.LoadingState.Success,
                                    fromAccounts = accounts,
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            mutableStateFlow.update {
                it.copy(
                    loadingState = InterbankTransferState.LoadingState.Error(
                        e.message ?: "Failed to load accounts",
                    ),
                )
            }
        }
    }

    private fun validateAndInitiateTransfer() {
        val validationError = validateTransferDetails()
        if (validationError != null) {
            mutableStateFlow.update {
                it.copy(errorMessage = validationError)
            }
            return
        }

        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(isProcessing = true)
            }

            val result = thirdPartyTransferRepository.makeTransfer(state.transferPayload)
            sendAction(InterbankTransferAction.Internal.HandleTransferResult(result))
        }
    }

    private fun validateTransferDetails(): String? {
        return when {
            state.selectedFromAccount == null -> "Please select a sender account"
            state.selectedRecipient == null -> "Please select a recipient"
            state.transferAmount.isBlank() -> "Please enter an amount"
            state.transferAmount.toDoubleOrNull() == null -> "Invalid amount"
            state.transferAmount.toDouble() <= 0 -> "Amount must be greater than 0"
            state.transferDescription.isBlank() -> "Please enter a description"
            else -> null
        }
    }

    private fun handleTransferResult(action: InterbankTransferAction.Internal.HandleTransferResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(isProcessing = true)
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        isProcessing = false,
                        currentStep = InterbankTransferState.Step.TransferSuccess,
                        transferResponse = action.result.data as String,
                    )
                }
                sendEvent(InterbankTransferEvent.OnTransferSuccess)
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        isProcessing = false,
                        currentStep = InterbankTransferState.Step.TransferFailed,
                        errorMessage = action.result.message,
                    )
                }
                sendEvent(InterbankTransferEvent.OnTransferFailed(action.result.message))
            }
        }
    }
}

@Serializable
data class InterbankTransferState(
    val client: Client,
    val currentStep: Step = Step.SelectAccount,
    val loadingState: LoadingState = LoadingState.Loading,
    val fromAccounts: List<Account> = emptyList(),
    val selectedFromAccount: Account? = null,
    val selectedRecipient: RecipientInfo? = null,
    val transferAmount: String = "",
    val transferDate: String = "",
    val transferDescription: String = "",
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val transferResponse: String? = null,
) {
    @Serializable
    sealed interface Step {
        @Serializable
        data object SelectAccount : Step

        @Serializable
        data object SearchRecipient : Step

        @Serializable
        data object TransferDetails : Step

        @Serializable
        data object PreviewTransfer : Step

        @Serializable
        data object TransferSuccess : Step

        @Serializable
        data object TransferFailed : Step
    }

    @Serializable
    sealed interface LoadingState {
        @Serializable
        data object Loading : LoadingState

        @Serializable
        data object Success : LoadingState

        @Serializable
        data class Error(val message: String) : LoadingState
    }

    val transferPayload: TransferPayload
        get() = TransferPayload(
            fromOfficeId = client.officeId.toInt(),
            fromClientId = client.id,
            fromAccountType = 2, // Savings account type
            fromAccountId = selectedFromAccount?.id?.toInt(),
            toOfficeId = selectedRecipient?.officeId,
            toClientId = selectedRecipient?.clientId,
            toAccountType = selectedRecipient?.accountType,
            toAccountId = selectedRecipient?.accountId,
            transferDate = transferDate,
            transferAmount = transferAmount.toDoubleOrNull() ?: 0.0,
            transferDescription = transferDescription,
            locale = "en_IN",
            dateFormat = "dd MMMM yyyy",
        )
}

@Serializable
data class RecipientInfo(
    val clientId: Long,
    val officeId: Int,
    val accountId: Int,
    val accountType: Int,
    val clientName: String,
    val accountNo: String,
)

sealed interface InterbankTransferEvent {
    data object OnNavigateBack : InterbankTransferEvent
    data object OnTransferSuccess : InterbankTransferEvent
    data class OnTransferFailed(val message: String) : InterbankTransferEvent
}

sealed interface InterbankTransferAction {
    // Navigation
    data class NavigateToRecipientSearch(val account: Account) : InterbankTransferAction
    data class NavigateToTransferDetails(val recipient: RecipientInfo) : InterbankTransferAction
    data object NavigateToPreview : InterbankTransferAction
    data object NavigateBack : InterbankTransferAction

    // Transfer details
    data class UpdateAmount(val amount: String) : InterbankTransferAction
    data class UpdateDate(val date: String) : InterbankTransferAction
    data class UpdateDescription(val description: String) : InterbankTransferAction

    // Transfer confirmation
    data object ConfirmTransfer : InterbankTransferAction
    data object RetryTransfer : InterbankTransferAction
    data object DismissError : InterbankTransferAction

    // Internal
    sealed interface Internal : InterbankTransferAction {
        data class HandleTransferResult(val result: DataState<Any>) : Internal
    }
}
