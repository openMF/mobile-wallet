/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.common.utils.capitalizeWords
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.util.UpiQrCodeProcessor
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.AccountTransferPayload
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.make.transfer.MakeTransferAction.Internal.HandleTransferResult
import org.mifospay.feature.make.transfer.MakeTransferState.DialogState.Error
import org.mifospay.feature.make.transfer.navigation.TRANSFER_ARG

private const val KEY_STATE = "make_transfer_state"

internal class MakeTransferViewModel(
    private val accountRepository: AccountRepository,
    repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MakeTransferState, MakeTransferEvent, MakeTransferAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val fromClientId = requireNotNull(repository.clientId.value)
        val defaultAccountId = requireNotNull(repository.defaultAccountId.value)
        val paymentData = requireNotNull(savedStateHandle.get<String>(TRANSFER_ARG))
        val clientData = UpiQrCodeProcessor.decodeUpiString(paymentData)

        MakeTransferState(
            fromClientId = fromClientId,
            defaultAccountId = defaultAccountId,
            toClientData = clientData,
        )
    },
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val accountsState = accountRepository.getSelfAccounts(state.fromClientId)
        .mapLatest { result ->
            when (result) {
                is DataState.Loading -> ViewState.Loading
                is DataState.Error -> ViewState.Error(result.message)
                is DataState.Success -> {
                    if (result.data.isEmpty()) {
                        ViewState.Empty
                    } else {
                        val account = result.data.first { it.id == state.defaultAccountId }
                        sendAction(MakeTransferAction.SelectAccount(account))
                        ViewState.Content(result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewState.Loading,
        )

    init {
        stateFlow.onEach { state ->
            savedStateHandle[KEY_STATE] = state
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: MakeTransferAction) {
        when (action) {
            MakeTransferAction.NavigateBack -> {
                sendEvent(MakeTransferEvent.OnNavigateBack)
            }

            is MakeTransferAction.AmountChanged -> {
                mutableStateFlow.update {
                    it.copy(amount = action.amount)
                }
            }

            is MakeTransferAction.DescriptionChanged -> {
                mutableStateFlow.update {
                    it.copy(description = action.desc)
                }
            }

            is MakeTransferAction.SelectAccount -> {
                mutableStateFlow.update {
                    it.copy(selectedAccount = action.account)
                }
            }

            is MakeTransferAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MakeTransferAction.InitiateTransfer -> validateTransfer()

            is HandleTransferResult -> handleTransferResult(action)
        }
    }

    private fun validateTransfer() = when {
        state.amount.isBlank() -> updateErrorState("Amount cannot be empty")

        state.amount.toDoubleOrNull() == null -> updateErrorState("Please enter a valid amount")

        state.description.isBlank() -> updateErrorState("Description cannot be empty")

        state.selectedAccount == null -> updateErrorState("Please select an account")

        state.selectedAccount?.status?.active == false -> {
            updateErrorState("Account is inactive")
        }

        state.selectedAccount?.id == state.toClientData.accountId -> {
            updateErrorState("Cannot transfer to the same account")
        }

        state.amount.toDouble() > state.selectedAccount?.balance!! -> {
            updateErrorState("Insufficient balance")
        }

        else -> initiateTransfer()
    }

    private fun initiateTransfer() {
        mutableStateFlow.update {
            it.copy(dialogState = MakeTransferState.DialogState.Loading)
        }

        viewModelScope.launch {
            val result = accountRepository.makeTransfer(state.transferPayload)

            sendAction(HandleTransferResult(result))
        }
    }

    private fun handleTransferResult(action: HandleTransferResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MakeTransferState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = Error(action.result.message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(MakeTransferEvent.OnTransferSuccess)
            }
        }
    }

    private fun updateErrorState(message: String) {
        mutableStateFlow.update {
            it.copy(dialogState = Error(message))
        }
    }
}

@Parcelize
internal data class MakeTransferState(
    val fromClientId: Long,
    val toClientData: PaymentQrData,
    val defaultAccountId: Long,
    val amount: String = toClientData.amount,
    val description: String = "",
    val selectedAccount: Account? = null,
    val dialogState: DialogState? = null,
) : Parcelable {

    @IgnoredOnParcel
    val amountIsValid: Boolean
        get() = amount.isNotEmpty() && amount.toDoubleOrNull() != null

    @IgnoredOnParcel
    val descriptionIsValid: Boolean
        get() = description.isNotEmpty()

    @IgnoredOnParcel
    val transferPayload: AccountTransferPayload
        get() = AccountTransferPayload(
            fromOfficeId = toClientData.officeId,
            fromClientId = fromClientId,
            fromAccountType = toClientData.accountTypeId,
            fromAccountId = selectedAccount?.id ?: defaultAccountId,
            toOfficeId = toClientData.officeId,
            toClientId = toClientData.clientId,
            toAccountType = toClientData.accountTypeId,
            toAccountId = toClientData.accountId,
            transferAmount = amount,
            transferDescription = description.capitalizeWords(),
            locale = "en_IN",
            dateFormat = DateHelper.SHORT_MONTH,
            transferDate = DateHelper.formattedShortDate,
        )

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }
}

internal sealed interface ViewState {
    data object Loading : ViewState
    data object Empty : ViewState
    data class Error(val message: String) : ViewState
    data class Content(val data: List<Account>) : ViewState
}

internal sealed interface MakeTransferEvent {
    data object OnNavigateBack : MakeTransferEvent
    data object OnTransferSuccess : MakeTransferEvent
}

internal sealed interface MakeTransferAction {
    data object NavigateBack : MakeTransferAction

    data object DismissDialog : MakeTransferAction

    data object InitiateTransfer : MakeTransferAction

    data class AmountChanged(val amount: String) : MakeTransferAction

    data class DescriptionChanged(val desc: String) : MakeTransferAction

    data class SelectAccount(val account: Account) : MakeTransferAction

    sealed interface Internal : MakeTransferAction {
        data class HandleTransferResult(val result: DataState<String>) : Internal
    }
}
