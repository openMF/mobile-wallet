package org.mifospay.feature.merchants

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mifospay.common.Constants
import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.TaskLooper.TaskData
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseFactory
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.core.data.util.Constants.FETCH_ACCOUNT_TRANSFER_USECASE
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.feature.HistoryContract
import org.mifospay.feature.TransactionsHistory
import javax.inject.Inject

@HiltViewModel
class MerchantTransferViewModel @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val preferencesHelper: PreferencesHelper,
    private val transactionsHistory: TransactionsHistory,
    private val mUseCaseFactory: UseCaseFactory,
    private val mFetchAccount: FetchAccount,
    private var mTaskLooper: TaskLooper? = null
) : ViewModel(), HistoryContract.TransactionsHistoryAsync {

    private val _uiState =
        MutableStateFlow<MerchantTransferUiState>(MerchantTransferUiState.Loading)
    val uiState: StateFlow<MerchantTransferUiState> = _uiState.asStateFlow()

    private val _merchantAccountNumber = MutableStateFlow("")
    private var merchantAccountNumber = _merchantAccountNumber.asStateFlow()

    fun checkBalanceAvailability(
        proceedWithMakeTransferFlow: (String, Double) -> Unit,
        externalId: String,
        transferAmount: Double,
    ) {
        mUsecaseHandler.execute(mFetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    if (transferAmount > response.account.balance) {
                        _uiState.value = MerchantTransferUiState.InsufficientBalance
                    } else {
                        proceedWithMakeTransferFlow(externalId, transferAmount)
                    }
                }

                override fun onError(message: String) {
                    _uiState.value = MerchantTransferUiState.Error
                }
            })
    }

    fun fetchMerchantTransfers(merchantAccountNumber: String) {
        transactionsHistory.fetchTransactionsHistory(preferencesHelper.accountId)
        _merchantAccountNumber.value = merchantAccountNumber
    }

    override fun onTransactionsFetchCompleted(transactions: List<Transaction>?) {
        val specificTransactions = ArrayList<Transaction>()
        val merchantAccountNumber = merchantAccountNumber.value

        if (!transactions.isNullOrEmpty()) {
            for (i in transactions.indices) {
                val transaction = transactions[i]
                if (transaction.transferDetail == null
                    && transaction.transferId != 0L
                ) {
                    val transferId = transaction.transferId
                    mTaskLooper?.addTask(
                        useCase = mUseCaseFactory.getUseCase(FETCH_ACCOUNT_TRANSFER_USECASE)
                                as UseCase<FetchAccountTransfer.RequestValues, FetchAccountTransfer.ResponseValue>,
                        values = transferId.let { FetchAccountTransfer.RequestValues(it) },
                        taskData = TaskData(Constants.TRANSFER_DETAILS, i)
                    )
                }
            }
            mTaskLooper?.listen(object : TaskLooper.Listener {
                override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                    taskData: TaskData, response: R
                ) {
                    when (taskData.taskName) {
                        Constants.TRANSFER_DETAILS -> {
                            val responseValue = response as FetchAccountTransfer.ResponseValue
                            val index = taskData.taskId
                            transactions[index].transferDetail = responseValue.transferDetail
                        }
                    }
                }

                override fun onComplete() {
                    for (transaction in transactions) {
                        if (transaction.transferDetail.toAccount
                                .accountNo == merchantAccountNumber
                        ) {
                            specificTransactions.add(transaction)
                        }
                    }
                    if (specificTransactions.size == 0) {
                        _uiState.value = MerchantTransferUiState.Empty
                    } else {
                        _uiState.value = MerchantTransferUiState.Success(specificTransactions)

                    }
                }

                override fun onFailure(message: String?) {
                    _uiState.value = MerchantTransferUiState.Error
                }
            })
        }
    }
}

sealed class MerchantTransferUiState {
    data object Loading : MerchantTransferUiState()
    class Success(val transactionsList: ArrayList<Transaction>) : MerchantTransferUiState()
    data object Empty : MerchantTransferUiState()
    data object Error : MerchantTransferUiState()
    data object InsufficientBalance : MerchantTransferUiState()
}
