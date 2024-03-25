package org.mifos.mobilewallet.mifospay.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import com.mifos.mobilewallet.model.domain.Account
import com.mifos.mobilewallet.model.domain.Transaction
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.core.datastore.PreferencesHelper
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionsHistoryAsync
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory
import javax.inject.Inject

/**
 * Created by naman on 17/8/17.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val preferencesHelper: PreferencesHelper,
    private val mFetchAccountUseCase: FetchAccount,
    private val transactionsHistory: TransactionsHistory
) : ViewModel(), TransactionsHistoryAsync {

    // Expose screen UI state
    private val _homeUIState = MutableStateFlow(HomeUiState())
    val homeUIState: StateFlow<HomeUiState> = _homeUIState.asStateFlow()

    init {
        fetchAccountDetails()
        fetchVpa()
    }

    private fun fetchAccountDetails() {
        mUsecaseHandler.execute(mFetchAccountUseCase,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    preferencesHelper.accountId = response.account.id
                    _homeUIState.update { currentState ->
                        currentState.copy(account = response.account)
                    }
                    response.account.id.let { transactionsHistory.fetchTransactionsHistory(it) }
                }

                override fun onError(message: String) {

                }
            })
    }

    private fun fetchVpa() {
        _homeUIState.update { currentState ->
            currentState.copy(vpa = localRepository.clientDetails.externalId)
        }
    }

    override fun onTransactionsFetchCompleted(transactions: List<Transaction>?) {
        _homeUIState.update { currentState ->
            currentState.copy(transactions = transactions ?: emptyList())
        }
    }
}

data class HomeUiState(
    val account: Account? = null,
    val transactions: List<Transaction> = emptyList(),
    val vpa: String? = null
)