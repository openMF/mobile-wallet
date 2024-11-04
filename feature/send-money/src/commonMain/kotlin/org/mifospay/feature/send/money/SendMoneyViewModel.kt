/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.util.UpiQrCodeProcessor
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.model.utils.toAccount
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.send.money.SendMoneyAction.HandleRequestData
import org.mifospay.feature.send.money.SendMoneyState.DialogState.Error

private const val KEY_STATE = "send_payment_state"

class SendMoneyViewModel(
    private val scanner: QrScanner,
    repository: AccountRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SendMoneyState, SendMoneyEvent, SendMoneyAction>(
    initialState = savedStateHandle[KEY_STATE] ?: SendMoneyState(),
) {

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val accountListState = stateFlow.map { it.accountNumber }
        .distinctUntilChanged()
        .debounce(300)
        .filter { it.length >= 4 }
        .flatMapLatest {
            repository.searchAccounts(it)
        }.mapLatest { result ->
            when (result) {
                is DataState.Loading -> ViewState.Loading
                is DataState.Error -> ViewState.Error(result.message)
                is DataState.Success -> {
                    if (result.data.isEmpty()) {
                        ViewState.Empty
                    } else {
                        ViewState.Content(result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewState.InitialEmpty,
        )

    init {
        stateFlow.onEach {
            savedStateHandle[KEY_STATE] = it
        }.launchIn(viewModelScope)

        savedStateHandle.get<String>("requestData")?.let {
            trySendAction(HandleRequestData(it))
        }
    }

    override fun handleAction(action: SendMoneyAction) {
        when (action) {
            is SendMoneyAction.AmountChanged -> {
                mutableStateFlow.update {
                    it.copy(amount = action.amount)
                }
            }

            is SendMoneyAction.AccountNumberChanged -> {
                mutableStateFlow.update {
                    it.copy(accountNumber = action.accountNumber)
                }
            }

            is SendMoneyAction.SelectAccount -> {
                mutableStateFlow.update {
                    it.copy(selectedAccount = action.account)
                }
            }

            SendMoneyAction.NavigateBack -> {
                sendEvent(SendMoneyEvent.OnNavigateBack)
            }

            SendMoneyAction.OnClickScan -> {
                scanner.startScanning().onEach { data ->
                    data?.let { result ->
                        sendAction(HandleRequestData(result))
                    }
                }.launchIn(viewModelScope)
                // Using Play Service Code Scanner until Qr Scan module is stable
                // sendEvent(SendMoneyEvent.NavigateToScanQrScreen)
            }

            is SendMoneyAction.DeselectAccount -> {
                mutableStateFlow.update {
                    it.copy(selectedAccount = null)
                }
            }

            SendMoneyAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            SendMoneyAction.OnProceedClicked -> validateTransferFlow()

            is HandleRequestData -> handleRequestData(action)
        }
    }

    private fun validateTransferFlow() = when {
        state.amount.isBlank() -> updateErrorState("Amount cannot be empty")

        state.amount.toDoubleOrNull() == null -> updateErrorState("Invalid amount")

        state.selectedAccount == null -> updateErrorState("Account cannot be empty")

        else -> initiateTransfer()
    }

    private fun initiateTransfer() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = null)
            }

            val paymentString = UpiQrCodeProcessor.encodeUpiString(state.paymentQrData)

            sendEvent(SendMoneyEvent.NavigateToTransferScreen(paymentString))
        }
    }

    private fun updateErrorState(message: String) {
        mutableStateFlow.update {
            it.copy(dialogState = Error(message))
        }
    }

    private fun handleRequestData(action: HandleRequestData) {
        viewModelScope.launch {
            try {
                val requestData = UpiQrCodeProcessor.decodeUpiString(action.requestData)

                mutableStateFlow.update { state ->
                    state.copy(
                        amount = requestData.amount,
                        accountNumber = requestData.accountNo,
                        selectedAccount = requestData.toAccount(),
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(dialogState = Error("Requesting payment QR but found - ${action.requestData}"))
                }
            }
        }
    }
}

@Parcelize
data class SendMoneyState(
    val amount: String = "",
    val accountNumber: String = "",
    val selectedAccount: AccountResult? = null,
    val dialogState: DialogState? = null,
) : Parcelable {
    @IgnoredOnParcel
    val amountIsValid: Boolean
        get() = amount.isNotEmpty() &&
            amount.toDoubleOrNull() != null &&
            amount.toDouble() > 0

    @IgnoredOnParcel
    val isProceedEnabled: Boolean
        get() = selectedAccount != null && amountIsValid

    @IgnoredOnParcel
    val paymentQrData: PaymentQrData
        get() = PaymentQrData(
            clientId = selectedAccount?.parentId ?: 0,
            clientName = selectedAccount?.parentName ?: "",
            accountNo = selectedAccount?.entityAccountNo ?: "",
            accountId = selectedAccount?.entityId ?: 0,
            amount = amount,
        )

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }
}

sealed interface ViewState {
    data object Loading : ViewState
    data class Error(val message: String) : ViewState
    data object Empty : ViewState
    data object InitialEmpty : ViewState
    data class Content(val data: List<AccountResult>) : ViewState
}

sealed interface SendMoneyEvent {
    data object OnNavigateBack : SendMoneyEvent
    data class NavigateToTransferScreen(val data: String) : SendMoneyEvent
    data object NavigateToScanQrScreen : SendMoneyEvent
}

sealed interface SendMoneyAction {
    data object NavigateBack : SendMoneyAction

    data object OnClickScan : SendMoneyAction

    data class AmountChanged(val amount: String) : SendMoneyAction

    data class AccountNumberChanged(val accountNumber: String) : SendMoneyAction

    data class SelectAccount(val account: AccountResult) : SendMoneyAction

    data object DeselectAccount : SendMoneyAction

    data object DismissDialog : SendMoneyAction

    data object OnProceedClicked : SendMoneyAction

    data class HandleRequestData(val requestData: String) : SendMoneyAction
}
