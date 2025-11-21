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
import org.mifospay.core.data.repository.InterBankRepository
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.interbank.Amount
import org.mifospay.core.model.interbank.InterBankPartyInfoResponse
import org.mifospay.core.model.interbank.InterBankTransferRequest
import org.mifospay.core.model.interbank.Party
import org.mifospay.core.model.interbank.TransactionType
import org.mifospay.core.ui.utils.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * ViewModel for managing interbank transfer flow
 * Handles all stages: account selection, recipient search, transfer details, preview, and confirmation
 */
class InterbankTransferViewModel(
    private val selfServiceRepository: SelfServiceRepository,
    private val interBankRepository: InterBankRepository,
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
                        selectedParticipantInfo = action.participantInfo,
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

            InterbankTransferAction.EditFromAccount -> {
                mutableStateFlow.update {
                    it.copy(currentStep = InterbankTransferState.Step.SelectAccount)
                }
            }

            InterbankTransferAction.EditRecipient -> {
                mutableStateFlow.update {
                    it.copy(currentStep = InterbankTransferState.Step.SearchRecipient)
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

            is InterbankTransferAction.SearchRecipient -> {
                searchRecipient(action.phoneNumber)
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

    @OptIn(ExperimentalUuidApi::class)
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

            // Build InterBank transfer request from participantInfo and selected account
            val participantInfo = state.selectedParticipantInfo ?: return@launch
            val transferRequest = InterBankTransferRequest(
                homeTransactionId = Uuid.random().toString(),
                from = Party(
                    fspId = participantInfo.sourceFspId,
                    idType = participantInfo.partyIdType,
                    idValue = state.selectedFromAccount?.externalId ?: state.selectedFromAccount?.number ?: "",
                ),
                to = Party(
                    fspId = participantInfo.destinationFspId,
                    idType = participantInfo.partyIdType,
                    idValue = participantInfo.partyId,
                ),
                amountType = "SEND",
                amount = Amount(
                    currencyCode = state.selectedFromAccount?.currency?.code ?: participantInfo.currencyCode,
                    amount = state.transferAmount.toDoubleOrNull() ?: 0.0,
                ),
                transactionType = TransactionType(
                    scenario = "TRANSFER",
                    subScenario = "DOMESTIC",
                    initiator = "PAYER",
                    initiatorType = "CUSTOMER"
                ),
                note = state.transferDescription,
            )

            val result = interBankRepository.interBankMakeTransfer(transferRequest)
            sendAction(InterbankTransferAction.Internal.HandleTransferResult(result))
        }
    }

    private fun validateTransferDetails(): String? {
        return when {
            state.selectedFromAccount == null -> "Please select a sender account"
            state.selectedParticipantInfo == null -> "Please select a recipient"
            state.transferAmount.isBlank() -> "Please enter an amount"
            state.transferAmount.toDoubleOrNull() == null -> "Invalid amount"
            state.transferAmount.toDouble() <= 0 -> "Amount must be greater than 0"
            state.transferDescription.isBlank() -> "Please enter a description"
            else -> null
        }
    }

    private fun searchRecipient(phoneNumber: String) {
        if (phoneNumber.length < 10) {
            mutableStateFlow.update {
                it.copy(
                    searchRecipientState = InterbankTransferState.SearchRecipientState.Error(
                        "Phone number must be at least 10 digits"
                    ),
                    searchResults = emptyList(),
                )
            }
            return
        }

        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(searchRecipientState = InterbankTransferState.SearchRecipientState.Loading)
            }

            val result = interBankRepository.findParticipant(
                partyId = phoneNumber,
                currencyCode = mutableStateFlow.value.selectedFromAccount?.currency?.code ?: "MXN",
            )

            when (result) {
                is DataState.Success -> {
                    val partyInfo = result.data
                    mutableStateFlow.update {
                        it.copy(
                            searchRecipientState = InterbankTransferState.SearchRecipientState.Success,
                            searchResults = listOf(partyInfo),
                        )
                    }
                }

                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            searchRecipientState = InterbankTransferState.SearchRecipientState.Error(
                                result.message ?: "Failed to search recipient"
                            ),
                            searchResults = emptyList(),
                        )
                    }
                }

                is DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(searchRecipientState = InterbankTransferState.SearchRecipientState.Loading)
                    }
                }
            }
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
                val transferResponse = action.result.data
                val responseMessage = when (transferResponse) {
                    is org.mifospay.core.model.interbank.InterBankTransferResponse -> {
                        "Transfer ID: ${transferResponse.transactionId}"
                    }
                    else -> "Transfer completed successfully"
                }
                
                mutableStateFlow.update {
                    it.copy(
                        isProcessing = false,
                        currentStep = InterbankTransferState.Step.TransferSuccess,
                        transferResponse = responseMessage,
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
    val selectedParticipantInfo: InterBankPartyInfoResponse? = null,
    val transferAmount: String = "1.0",
    val transferDate: String = "",
    val transferDescription: String = "",
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val transferResponse: String? = null,
    val searchRecipientState: SearchRecipientState = SearchRecipientState.Idle,
    val searchResults: List<InterBankPartyInfoResponse> = emptyList(),
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

    @Serializable
    sealed interface SearchRecipientState {
        @Serializable
        data object Idle : SearchRecipientState

        @Serializable
        data object Loading : SearchRecipientState

        @Serializable
        data object Success : SearchRecipientState

        @Serializable
        data class Error(val message: String) : SearchRecipientState
    }

}

sealed interface InterbankTransferEvent {
    data object OnNavigateBack : InterbankTransferEvent
    data object OnTransferSuccess : InterbankTransferEvent
    data class OnTransferFailed(val message: String) : InterbankTransferEvent
}

sealed interface InterbankTransferAction {
    // Navigation
    data class NavigateToRecipientSearch(val account: Account) : InterbankTransferAction
    data class NavigateToTransferDetails(val participantInfo: InterBankPartyInfoResponse) : InterbankTransferAction
    data object NavigateToPreview : InterbankTransferAction
    data object NavigateBack : InterbankTransferAction
    data object EditFromAccount : InterbankTransferAction
    data object EditRecipient : InterbankTransferAction

    // Transfer details
    data class UpdateAmount(val amount: String) : InterbankTransferAction
    data class UpdateDate(val date: String) : InterbankTransferAction
    data class UpdateDescription(val description: String) : InterbankTransferAction

    // Recipient search
    data class SearchRecipient(val phoneNumber: String) : InterbankTransferAction

    // Transfer confirmation
    data object ConfirmTransfer : InterbankTransferAction
    data object RetryTransfer : InterbankTransferAction
    data object DismissError : InterbankTransferAction

    // Internal
    sealed interface Internal : InterbankTransferAction {
        data class HandleTransferResult(val result: DataState<Any>) : Internal
    }
}
