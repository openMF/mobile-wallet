package org.mifospay.feature.make.transfer.v2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mobile_wallet.feature.make_transfer.generated.resources.Res
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_empty_amount
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_empty_description
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_inactive_account
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_insufficient_balance
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_invalid_amount
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_same_account
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_error_select_account
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.StringResourceSerializer
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.common.utils.capitalizeWords
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.util.UpiQrCodeProcessor
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.AccountTransferPayload
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.make.transfer.navigation.TRANSFER_ARG


internal class MakeTransferV2ScreenV2ViewModel(
    private val accountRepository: AccountRepository,
    repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MakeTransferV2State, MakeTransferV2Event, MakeTransferV2Action>(
    initialState = run {
        val route=savedStateHandle.toRoute<MakeTransferScreenV2Route>()
        val fromClientId = requireNotNull(repository.clientId.value)
        val defaultAccountId = requireNotNull(repository.defaultAccountId.value)
        val clientData=PaymentQrData(
            clientId = route.clientId,
            clientName = route.clientName,
            accountNo = route.accountNo,
            amount = route.amount.toString(),
            accountId = route.accountId,
        )
        MakeTransferV2State(
            fromClientId = fromClientId,
            defaultAccountId = defaultAccountId,
            toClientData = clientData,
        )
    }
) {

    companion object {
        private const val KEY_STATE = "make_transfer_state"
    }

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
                        sendAction(MakeTransferV2Action.SelectAccount(account))
                        val activeAccounts = result.data.filter {
                            it.status.active
                        }
                        ViewState.Content(activeAccounts)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewState.Loading,
        )

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: MakeTransferV2Action) {
        when (action) {
            MakeTransferV2Action.NavigateBack -> {
                sendEvent(MakeTransferV2Event.OnNavigateBack)
            }

            is MakeTransferV2Action.AmountChanged -> {
                mutableStateFlow.update {
                    it.copy(amount = action.amount)
                }
            }

            is MakeTransferV2Action.DescriptionChanged -> {
                mutableStateFlow.update {
                    it.copy(description = action.desc)
                }
            }

            is MakeTransferV2Action.SelectAccount -> {
                mutableStateFlow.update {
                    it.copy(selectedAccount = action.account)
                }
            }

            is MakeTransferV2Action.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MakeTransferV2Action.InitiateTransfer -> validateTransfer()

            is MakeTransferV2Action.Internal.HandleTransferResult -> handleTransferResult(action)
        }
    }

    private fun validateTransfer() = when {
        state.amount.isBlank() -> updateErrorState(Res.string.feature_make_transfer_error_empty_amount)

        state.amount.toDoubleOrNull() == null -> updateErrorState(Res.string.feature_make_transfer_error_invalid_amount)

        state.description.isBlank() -> updateErrorState(Res.string.feature_make_transfer_error_empty_description)

        state.selectedAccount == null -> updateErrorState(Res.string.feature_make_transfer_error_select_account)

        state.selectedAccount?.status?.active == false -> {
            updateErrorState(Res.string.feature_make_transfer_error_inactive_account)
        }

        state.selectedAccount?.id == state.toClientData.accountId -> {
            updateErrorState(Res.string.feature_make_transfer_error_same_account)
        }

        state.amount.toDouble() > state.selectedAccount?.balance!! -> {
            updateErrorState(Res.string.feature_make_transfer_error_insufficient_balance)
        }

        else -> initiateTransfer()
    }

    private fun initiateTransfer() {
        mutableStateFlow.update {
            it.copy(dialogState = MakeTransferV2State.DialogState.Loading)
        }

        viewModelScope.launch {
            val result = accountRepository.makeTransfer(state.transferPayload)

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
    val fromClientId: Long,
    val toClientData: PaymentQrData,
    val defaultAccountId: Long,
    val amount: String = toClientData.amount,
    val description: String = "",
    val selectedAccount: Account? = null,
    val dialogState: DialogState? = null,
) {
    val amountIsValid: Boolean
        get() = amount.isNotEmpty() && amount.toDoubleOrNull() != null

    val descriptionIsValid: Boolean
        get() = description.isNotEmpty()

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
}

internal sealed interface ViewState {
    data object Loading : ViewState
    data object Empty : ViewState
    data class Error(val message: String) : ViewState
    data class Content(val data: List<Account>) : ViewState
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

    data class SelectAccount(val account: Account) : MakeTransferV2Action

    sealed interface Internal : MakeTransferV2Action {
        data class HandleTransferResult(val result: DataState<String>) : Internal
    }
}
