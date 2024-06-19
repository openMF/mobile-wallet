package org.mifospay.feature.specific.transactions

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseFactory
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.data.util.Constants
import javax.inject.Inject

@HiltViewModel
class SpecificTransactionsViewModel @Inject constructor(
    private val mUseCaseFactory: UseCaseFactory,
    private var mTaskLooper: TaskLooper? = null
) : ViewModel() {

    private val _uiState: MutableStateFlow<SpecificTransactionsUiState> =
        MutableStateFlow(SpecificTransactionsUiState.Loading)
    val uiState get() = _uiState

    private val _transactions: MutableStateFlow<ArrayList<Transaction>> =
        MutableStateFlow(ArrayList())
    private val transactions get() = _transactions

    private val _accountNumber: MutableStateFlow<String> = MutableStateFlow("")
    private val accountNumber get() = _accountNumber

    fun getSpecificTransactions(): ArrayList<Transaction> {
        val transactions = transactions.value
        val accountNumber = accountNumber.value

        val specificTransactions = ArrayList<Transaction>()
        if (transactions.size > 0) {
            for (i in transactions.indices) {
                val transaction = transactions[i]
                val transferId = transaction.transferId
                mTaskLooper?.addTask(
                    useCase = mUseCaseFactory.getUseCase(Constants.FETCH_ACCOUNT_TRANSFER_USECASE)
                            as UseCase<FetchAccountTransfer.RequestValues, FetchAccountTransfer.ResponseValue>,
                    values = FetchAccountTransfer.RequestValues(transferId),
                    taskData = TaskLooper.TaskData(
                        org.mifospay.common.Constants.TRANSFER_DETAILS, i
                    )
                )
            }
            mTaskLooper!!.listen(object : TaskLooper.Listener {
                override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                    taskData: TaskLooper.TaskData, response: R
                ) {
                    when (taskData.taskName) {
                        org.mifospay.common.Constants.TRANSFER_DETAILS -> {
                            val responseValue = response as FetchAccountTransfer.ResponseValue
                            val index = taskData.taskId
                            transactions[index].transferDetail = responseValue.transferDetail
                        }
                    }
                }

                override fun onComplete() {
                    for (transaction in transactions) {
                        if (
                            transaction.transferDetail.fromAccount.accountNo == accountNumber ||
                            transaction.transferDetail.toAccount.accountNo == accountNumber
                        ) {
                            specificTransactions.add(transaction)
                        }
                    }
                    _uiState.value = SpecificTransactionsUiState.Success(specificTransactions)
                }

                override fun onFailure(message: String?) {
                    _uiState.value = SpecificTransactionsUiState.Error
                }
            })
        }
        return specificTransactions
    }

    fun setArguments(transactions: ArrayList<Transaction>, accountNumber: String) {
        _accountNumber.value = accountNumber
        _transactions.value = transactions
    }
}

sealed class SpecificTransactionsUiState {
    data object Loading : SpecificTransactionsUiState()
    data object Error : SpecificTransactionsUiState()
    data class Success(val transactionsList: ArrayList<Transaction>) : SpecificTransactionsUiState()
}