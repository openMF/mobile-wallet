package org.mifospay.feature.send.money.selectScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.StringResourceSerializer
import org.mifospay.core.common.getSerialized
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.send.money.QrScanner
import org.mifospay.feature.send.money.SendMoneyAction


class SelectScreenViewModel(
    private val scanner: QrScanner,
    repository: AccountRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SelectScreenState, SelectScreenEvent, SelectScreenAction>(
    initialState = SelectScreenState(),
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
                is DataState.Loading -> v2.ViewState.Loading
                is DataState.Error -> v2.ViewState.Error(result.message)
                is DataState.Success -> {
                    if (result.data.isEmpty()) {
                        v2.ViewState.Empty
                    } else {
                        v2.ViewState.Content(result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = v2.ViewState.InitialEmpty,
        )

    override fun handleAction(action: SelectScreenAction) {
        when(action){
            is SelectScreenAction.AccountNumberChanged -> {
                mutableStateFlow.update {
                    it.copy(accountNumber = action.accountNumber)
                }
            }
            SelectScreenAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
            SelectScreenAction.NavigateBack -> {

            }
            is SelectScreenAction.SelectAccount -> {
                mutableStateFlow.update {
                    it.copy(selectedAccount = action.account)
                }
            }

            SelectScreenAction.DeselectAccount -> {
                mutableStateFlow.update {
                    it.copy(selectedAccount = null)
                }
            }

            SelectScreenAction.OnProceedClicked -> {

            }
        }
    }
}


@Serializable
data class SelectScreenState(
    val amount: String = "",
    val accountNumber: String = "",
    val selectedAccount: AccountResult? = null,
    val dialogState: DialogState? = null,
) {

    val isProceedEnabled: Boolean
        get() = selectedAccount != null

    @Serializable
    sealed interface DialogState {
        @Serializable
        data object Loading : DialogState

        @Serializable
        sealed class Error : DialogState {
            @Serializable
            data class ResourceMessage(
                @Serializable(with = StringResourceSerializer::class)
                val message: StringResource,
            ) : Error()

            @Serializable
            data class GenericResourceMessage(
                @Serializable(with = StringResourceSerializer::class)
                val message: StringResource,
                val args: List<String>,
            ) : Error()
        }
    }
}


sealed interface SelectScreenEvent {
    data class NavigateToTransferScreen(val data: String) : SelectScreenEvent
}

sealed interface SelectScreenAction {
    data object NavigateBack : SelectScreenAction

    data class AccountNumberChanged(val accountNumber: String) : SelectScreenAction

    data class SelectAccount(val account: AccountResult) : SelectScreenAction

    data object DismissDialog : SelectScreenAction

    data object DeselectAccount : SelectScreenAction

    data object OnProceedClicked : SelectScreenAction
}
