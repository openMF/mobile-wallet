package org.mifospay.home

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Account
import com.mifospay.core.model.domain.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.data.local.LocalRepository
import org.mifospay.feature.HistoryContract
import org.mifospay.feature.TransactionsHistory
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val preferencesHelper: PreferencesHelper,
    private val fetchAccountUseCase: FetchAccount,
    private val transactionsHistory: TransactionsHistory
) : ViewModel(), HistoryContract.TransactionsHistoryAsync {

    // Expose screen UI state
    private val _homeUIState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val homeUIState: StateFlow<HomeUiState> = _homeUIState.asStateFlow()

    init {
        transactionsHistory.delegate = this
        fetchAccountDetails()
    }

    fun fetchAccountDetails() {
        useCaseHandler.execute(fetchAccountUseCase,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    preferencesHelper.accountId = response.account.id
                    _homeUIState.update {
                        HomeUiState.Success(
                            account = response.account,
                            vpa = localRepository.clientDetails.externalId
                        )
                    }
                    response.account.id.let {
                        transactionsHistory.fetchTransactionsHistory(it)
                    }
                }

                override fun onError(message: String) {
                    _homeUIState.update { HomeUiState.Error }
                }
            })
    }

    override fun onTransactionsFetchCompleted(transactions: List<Transaction>?) {
        _homeUIState.update { currentState ->
            (currentState as HomeUiState.Success)
                .copy(transactions = transactions ?: emptyList())
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val account: Account? = null,
        val transactions: List<Transaction> = emptyList(),
        val vpa: String? = null
    ) : HomeUiState

    data object Error : HomeUiState
}