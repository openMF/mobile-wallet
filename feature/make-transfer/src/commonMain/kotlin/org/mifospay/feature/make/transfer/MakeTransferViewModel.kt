/*
 * Copyright 2025 Mifos Initiative
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
import androidx.navigation.toRoute
import io.ktor.client.request.invoke
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mobile_wallet.feature.make_transfer.generated.resources.Res
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_empty_amount
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_empty_description
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_insufficient_balance
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_invalid_amount
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_same_account
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_select_account
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.StringResourceSerializer
import org.mifospay.core.common.utils.capitalizeWords
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.network.model.entity.TPTResponse
import org.mifospay.core.network.model.entity.payload.TransferPayload
import org.mifospay.core.network.model.entity.templates.account.AccountOption
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.make.transfer.navigation.MakeTransferRoute
import kotlin.Int

internal class MakeTransferViewModel(
    private val repository: ThirdPartyTransferRepository,
    private val clientRepo: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MakeTransferState, MakeTransferEvent, MakeTransferAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<MakeTransferRoute>()
        MakeTransferState(
            toOfficeId = route.toOfficeId,
            toClientId = route.toClientId,
            toAccountType = route.toAccountTypeId,
            toAccountId = route.accountId.toInt(),
            toAccountName = route.toAccountName,
            toAccountNo = route.toAccountNo,
            amount = if (route.amount == 0) "" else route.amount.toString(),
        )
    },
) {

    init {
        viewModelScope.launch {
            getFromAccounts()
        }
    }
    override fun handleAction(action: MakeTransferAction) {
        when (action) {
            MakeTransferAction.NavigateBack -> {
                sendEvent(MakeTransferEvent.OnNavigateBack)
            }

            is MakeTransferAction.AmountChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        amount = action.amount,
                    )
                }
            }

            is MakeTransferAction.DescriptionChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        description = action.desc,
                    )
                }
            }

            is MakeTransferAction.SelectAccount -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedAccount = action.account,
                        selectedAccountBalance = state.balanceMap.getOrElse(
                            action.account?.accountNo ?: "",
                        ) { 0.0 },
                        showBottomSheet = false,
                    )
                }
            }

            is MakeTransferAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MakeTransferAction.InitiateTransfer -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MakeTransferState.DialogState.Loading)
                }
                validateTransfer()
            }

            is MakeTransferAction.Internal.HandleTransferResult -> handleTransferResult(action)

            MakeTransferAction.CloseBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            MakeTransferAction.OpenBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = true)
                }
            }
        }
    }

    private suspend fun getFromAccounts() {
        try {
            val res = repository.getTransferTemplate()
            val fromAccounts = res.fromAccountOptions?.filter {
                it.accountType?.id == 2
            }
            if (fromAccounts.isNullOrEmpty()) {
                mutableStateFlow.update {
                    it.copy(
                        state = MakeTransferState.State.NoAccounts,
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        fromAccountOptions = fromAccounts,
                    )
                }
                getBalanceOfAccounts()
            }
        } catch (e: Exception) {
            mutableStateFlow.update {
                it.copy(
                    state = MakeTransferState.State.Error(e.message ?: ""),
                )
            }
        }
    }

    private suspend fun getBalanceOfAccounts() {
        try {
            val clientId = state.fromAccountOptions?.first()?.clientId ?: -1
            val result = clientRepo.getClientAccounts(clientId)
            val balanceMap: Map<String, Double> =
                result.savingsAccounts.associate { account ->
                    account.accountNo to account.accountBalance
                }
            mutableStateFlow.update {
                it.copy(
                    state = MakeTransferState.State.Success,
                    balanceMap = balanceMap,
                )
            }
            val firstAccount = state.fromAccountOptions?.first()
            mutableStateFlow.update {
                it.copy(
                    selectedAccount = firstAccount,
                    selectedAccountBalance = balanceMap.getOrElse(
                        firstAccount?.accountNo ?: "",
                        { 0.0 },
                    ),
                )
            }
        } catch (e: Exception) {
            mutableStateFlow.update {
                it.copy(
                    state = MakeTransferState.State.Error(e.message ?: ""),
                )
            }
        }
    }

    private fun validateTransfer() = when {
        state.amount.isBlank() -> updateErrorState(Res.string.feature_make_transfer_error_empty_amount)

        state.amount.toDoubleOrNull() == null -> updateErrorState(Res.string.feature_make_transfer_error_invalid_amount)

        state.description.isBlank() -> updateErrorState(Res.string.feature_make_transfer_error_empty_description)

        state.selectedAccount == null -> updateErrorState(Res.string.feature_make_transfer_error_select_account)

        state.selectedAccount?.accountId == state.toAccountId -> {
            updateErrorState(Res.string.feature_make_transfer_error_same_account)
        }

        state.amount.toDouble() > state.selectedAccountBalance -> {
            updateErrorState(Res.string.feature_make_transfer_error_insufficient_balance)
        }

        else -> initiateTransfer()
    }

    private fun initiateTransfer() {
        viewModelScope.launch {
            val result = repository.makeTransfer(state.transferPayload)

            sendAction(MakeTransferAction.Internal.HandleTransferResult(result))
        }
    }

    private fun handleTransferResult(action: MakeTransferAction.Internal.HandleTransferResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MakeTransferState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MakeTransferState.DialogState.Error.StringMessage(action.result.message))
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

    private fun updateErrorState(message: StringResource) {
        mutableStateFlow.update {
            it.copy(dialogState = MakeTransferState.DialogState.Error.ResourceMessage(message))
        }
    }
}

@Serializable
internal data class MakeTransferState(
    val toOfficeId: Int? = null,
    val toClientId: Long? = null,
    val toAccountType: Int? = null,
    val toAccountId: Int? = null,
    val toAccountName: String = "",
    val toAccountNo: String = "",
    val amount: String = "",

    val showBottomSheet: Boolean = false,
    val state: State = State.Loading,
    val description: String = " ",
    val selectedAccount: AccountOption? = null,
    val selectedAccountBalance: Double = 0.0,
    val dialogState: DialogState? = null,
    val fromAccountOptions: List<AccountOption>? = emptyList(),
    val balanceMap: Map<String, Double> = emptyMap(),
) {
    val amountIsValid: Boolean
        get() = amount.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDouble() <= selectedAccountBalance

    val descriptionIsValid: Boolean
        get() = description.trim().isNotEmpty()

    val transferPayload: TransferPayload
        get() = TransferPayload(
            fromOfficeId = selectedAccount?.officeId,
            fromClientId = selectedAccount?.clientId,
            fromAccountType = selectedAccount?.accountType?.id,
            fromAccountId = selectedAccount?.accountId,
            toOfficeId = toOfficeId,
            toClientId = toClientId,
            toAccountType = toAccountType,
            toAccountId = toAccountId,
            transferDate = DateHelper.formattedShortDate,
            transferAmount = amount.toDoubleOrNull() ?: 0.0,
            transferDescription = description.trim().capitalizeWords(),
            locale = "en_IN",
            dateFormat = DateHelper.SHORT_MONTH,
        )

    @Serializable
    sealed interface DialogState {
        @Serializable
        data object Loading : DialogState

        @Serializable
        sealed class Error : DialogState {
            @Serializable
            data class StringMessage(val message: String) : Error()

            @Serializable
            data class ResourceMessage(
                @Serializable(with = StringResourceSerializer::class)
                val message: StringResource,
            ) : Error()
        }
    }

    sealed interface State {
        data object Loading : State
        data object NoAccounts : State
        data object Success : State
        data class Error(val message: String) : State
    }
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

    data class SelectAccount(val account: AccountOption?) : MakeTransferAction

    sealed interface Internal : MakeTransferAction {
        data class HandleTransferResult(val result: DataState<TPTResponse>) : Internal
    }

    data object OpenBottomSheet : MakeTransferAction
    data object CloseBottomSheet : MakeTransferAction
}
