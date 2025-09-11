/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.v2

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
import kotlin.Int

internal class MakeTransferV2ScreenV2ViewModel(
    private val repository: ThirdPartyTransferRepository,
    private val clientRepo: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MakeTransferV2State, MakeTransferV2Event, MakeTransferV2Action>(
    initialState = run {
        val route = savedStateHandle.toRoute<MakeTransferScreenV2Route>()
        MakeTransferV2State(
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
    override fun handleAction(action: MakeTransferV2Action) {
        when (action) {
            MakeTransferV2Action.NavigateBack -> {
                sendEvent(MakeTransferV2Event.OnNavigateBack)
            }

            is MakeTransferV2Action.AmountChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        amount = action.amount,
                    )
                }
            }

            is MakeTransferV2Action.DescriptionChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        description = action.desc,
                    )
                }
            }

            is MakeTransferV2Action.SelectAccount -> {
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

            is MakeTransferV2Action.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MakeTransferV2Action.InitiateTransfer -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MakeTransferV2State.DialogState.Loading)
                }
                validateTransfer()
            }

            is MakeTransferV2Action.Internal.HandleTransferResult -> handleTransferResult(action)

            MakeTransferV2Action.CloseBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            MakeTransferV2Action.OpenBottomSheet -> {
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
                        state = MakeTransferV2State.State.NoAccounts,
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
                    state = MakeTransferV2State.State.Error(e.message ?: ""),
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
                    state = MakeTransferV2State.State.Success,
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
                    state = MakeTransferV2State.State.Error(e.message ?: ""),
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

            sendAction(MakeTransferV2Action.Internal.HandleTransferResult(result))
        }
    }

    private fun handleTransferResult(action: MakeTransferV2Action.Internal.HandleTransferResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MakeTransferV2State.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MakeTransferV2State.DialogState.Error.StringMessage(action.result.message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(MakeTransferV2Event.OnTransferSuccess)
            }
        }
    }

    private fun updateErrorState(message: StringResource) {
        mutableStateFlow.update {
            it.copy(dialogState = MakeTransferV2State.DialogState.Error.ResourceMessage(message))
        }
    }
}

@Serializable
internal data class MakeTransferV2State(
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

internal sealed interface MakeTransferV2Event {
    data object OnNavigateBack : MakeTransferV2Event
    data object OnTransferSuccess : MakeTransferV2Event
}

internal sealed interface MakeTransferV2Action {
    data object NavigateBack : MakeTransferV2Action

    data object DismissDialog : MakeTransferV2Action

    data object InitiateTransfer : MakeTransferV2Action

    data class AmountChanged(val amount: String) : MakeTransferV2Action

    data class DescriptionChanged(val desc: String) : MakeTransferV2Action

    data class SelectAccount(val account: AccountOption?) : MakeTransferV2Action

    sealed interface Internal : MakeTransferV2Action {
        data class HandleTransferResult(val result: DataState<TPTResponse>) : Internal
    }

    data object OpenBottomSheet : MakeTransferV2Action
    data object CloseBottomSheet : MakeTransferV2Action
}
