package org.mifospay.feature.history

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransactions
import org.mifospay.core.data.repository.local.LocalRepository
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

    private fun fetchTransactions() {
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
}

sealed class HistoryUiState {
    data object Loading : HistoryUiState()
    data object Empty : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
    data class HistoryList(val list: List<Transaction>) : HistoryUiState()
}