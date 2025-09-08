package v2

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
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.StringResourceSerializer
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.send.money.QrScanner


class SendMoneyV2ViewModel(
    repository: AccountRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SendMoneyV2State, SendMoneyV2Event, SendMoneyV2Action>(
    initialState = SendMoneyV2State(),
) {



    
    override fun handleAction(action: SendMoneyV2Action) {
        when(action){
            SendMoneyV2Action.NavigateBack -> {
                sendEvent(SendMoneyV2Event.NavigateBack)
            }

            SendMoneyV2Action.OnSearchBarClicked -> {
                sendEvent(SendMoneyV2Event.NavigateToSearchAccountSelection)
            }
        }
    }

}


@Serializable
data class SendMoneyV2State(
    val amount: String = "",
    val accountNumber: String = "",
    val selectedAccount: AccountResult? = null,
    val dialogState: DialogState? = null,
) {

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

sealed interface ViewState {
    data object Loading : ViewState
    data class Error(val message: String) : ViewState
    data object Empty : ViewState
    data object InitialEmpty : ViewState
    data class Content(val data: List<AccountResult>) : ViewState
}

sealed interface SendMoneyV2Event {
    data object NavigateToSearchAccountSelection: SendMoneyV2Event
    data object NavigateBack:SendMoneyV2Event
}

sealed interface SendMoneyV2Action {
    data object NavigateBack : SendMoneyV2Action

    data object OnSearchBarClicked : SendMoneyV2Action

}
