package org.mifos.mobilewallet.mifospay.history.presenter

import androidx.lifecycle.ViewModel
import com.mifos.mobilewallet.model.domain.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val mFetchAccountUseCase: FetchAccount,
    private val fetchAccountTransactionsUseCase: FetchAccountTransactions
) : ViewModel() {

    private val _historyUiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val historyUiState: StateFlow<HistoryUiState> = _historyUiState

    val isRefreshing = MutableStateFlow(false)

    fun fetchTransactions() {
        _historyUiState.value = HistoryUiState.Loading
        mUseCaseHandler.execute(mFetchAccountUseCase,
            FetchAccount.RequestValues(mLocalRepository.clientDetails.clientId),
            object : UseCase.UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    response.account.id.let {
                        fetchTransactionsHistory(it)
                    }
                }

                override fun onError(message: String) {
                    _historyUiState.value = HistoryUiState.Error(message)
                }
            })
    }

    fun fetchTransactionsHistory(accountId: Long) {
        mUseCaseHandler.execute(fetchAccountTransactionsUseCase,
            FetchAccountTransactions.RequestValues(accountId),
            object : UseCase.UseCaseCallback<FetchAccountTransactions.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransactions.ResponseValue?) {
                    if (response?.transactions?.isNotEmpty() == true)
                        _historyUiState.value = HistoryUiState.HistoryList(response.transactions)
                    else _historyUiState.value = HistoryUiState.Empty
                }

                override fun onError(message: String) {
                    _historyUiState.value = HistoryUiState.Error(message)
                }
            })
    }

    init {
        fetchTransactions()
    }
    fun refreshTransactionHistory(){
        isRefreshing.value = true
        fetchTransactions()
        isRefreshing.value = false
    }
}

sealed class HistoryUiState {
    data object Loading : HistoryUiState()
    data object Empty : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
    data class HistoryList(val list: List<Transaction>) : HistoryUiState()
}