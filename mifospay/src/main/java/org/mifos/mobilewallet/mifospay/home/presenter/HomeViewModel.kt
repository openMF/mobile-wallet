package org.mifos.mobilewallet.mifospay.home.presenter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mifos.mobilewallet.core.base.TaskLooper
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseFactory
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.model.Account
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionsHistoryAsync
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.HomeView
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * Created by naman on 17/8/17.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel(), BaseHomeContract.HomePresenter, TransactionsHistoryAsync {

    @JvmField
    @Inject
    var mFetchAccountUseCase: FetchAccount? = null

    @JvmField
    @Inject
    var fetchAccountTransactionsUseCase: FetchAccountTransactions? = null

    @JvmField
    @Inject
    var mTaskLooper: TaskLooper? = null

    @JvmField
    @Inject
    var mUseCaseFactory: UseCaseFactory? = null

    @JvmField
    @Inject
    var transactionsHistory: TransactionsHistory? = null
    private var mHomeView: HomeView? = null
    private var transactionList: List<Transaction>? = null

    // Expose screen UI state
    private val _homeUIState = MutableStateFlow(HomeUiState())
    val homeUIState: StateFlow<HomeUiState> = _homeUIState.asStateFlow()

    override fun attachView(baseView: BaseView<*>?) {
        mHomeView = baseView as HomeView?
        mHomeView?.setPresenter(this)
        transactionsHistory?.delegate = this
    }

    override fun fetchAccountDetails() {
        mUsecaseHandler.execute(mFetchAccountUseCase,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue?> {
                override fun onSuccess(response: FetchAccount.ResponseValue?) {
                    preferencesHelper.accountId = response?.account?.id!!

                    _homeUIState.update { currentState ->
                        currentState.copy(account = response.account)
                    }

                    mHomeView?.setAccountBalance(response.account)
                    response.account?.id?.let { transactionsHistory?.fetchTransactionsHistory(it) }
                    mHomeView?.hideSwipeProgress()
                }

                override fun onError(message: String) {
                    mHomeView?.hideBottomSheetActionButton()
                    mHomeView?.showTransactionsError()
                    mHomeView?.showToast(message)
                    mHomeView?.hideSwipeProgress()
                    mHomeView?.hideTransactionLoading()
                }
            })
    }

    private fun handleTransactionsHistory(existingItemCount: Int) {
        val transactionsAmount = transactionList?.size?.minus(existingItemCount)
        if (transactionsAmount != null) {
            if (transactionsAmount > Constants.HOME_HISTORY_TRANSACTIONS_LIMIT) {
                val showList = transactionList?.subList(
                    0,
                    Constants.HOME_HISTORY_TRANSACTIONS_LIMIT + existingItemCount
                )
                _homeUIState.update { currentState ->
                    currentState.copy(transactions = showList ?: emptyList())
                }
                mHomeView?.showTransactionsHistory(showList)
                mHomeView?.showBottomSheetActionButton()
            } else {
                if (transactionsAmount <= Constants.HOME_HISTORY_TRANSACTIONS_LIMIT
                    && transactionsAmount > 0
                ) {
                    _homeUIState.update { currentState ->
                        currentState.copy(transactions = transactionList ?: emptyList())
                    }
                    mHomeView?.showTransactionsHistory(transactionList)
                    mHomeView?.hideBottomSheetActionButton()
                } else {
                    mHomeView?.showTransactionsEmpty()
                }
            }
        }
    }

    override fun showMoreHistory(existingItemCount: Int) {
        if (transactionList?.size == existingItemCount) {
            mHomeView?.showToast("No more History Available")
        } else {
            handleTransactionsHistory(existingItemCount)
        }
    }

    override fun fetchVpa() {
        _homeUIState.update { currentState ->
            currentState.copy(vpa = localRepository.clientDetails.externalId)
        }
    }

    override fun onTransactionsFetchCompleted(transactions: List<Transaction>?) {
        transactionList = transactions
        if (transactionList == null) {
            mHomeView?.hideBottomSheetActionButton()
            mHomeView?.showTransactionsError()
        } else {
            handleTransactionsHistory(0)
        }
    }
}

data class HomeUiState(
    val account: Account? = null,
    val transactions: List<Transaction> = emptyList(),
    val vpa: String? = null
)