/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.specific.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseFactory
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.data.util.Constants
import org.mifospay.feature.specific.transactions.SpecificTransactionsUiState.Loading
import org.mifospay.feature.specific.transactions.navigation.ACCOUNT_NUMBER_ARG
import org.mifospay.feature.specific.transactions.navigation.TRANSACTIONS_ARG

class SpecificTransactionsViewModel(
    private val mUseCaseFactory: UseCaseFactory,
    private var mTaskLooper: TaskLooper? = null,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SpecificTransactionsUiState> = MutableStateFlow(Loading)
    val uiState = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>(ACCOUNT_NUMBER_ARG)?.let { accountNumber ->
            savedStateHandle.get<ArrayList<Transaction>>(TRANSACTIONS_ARG)?.let { transactions ->
                getSpecificTransactions(accountNumber, transactions)
            }
        }
    }

    private fun getSpecificTransactions(
        accountNumber: String,
        transactions: ArrayList<Transaction>,
    ): ArrayList<Transaction> {
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
                        org.mifospay.common.Constants.TRANSFER_DETAILS,
                        i,
                    ),
                )
            }
            mTaskLooper!!.listen(
                object : TaskLooper.Listener {
                    override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                        taskData: TaskLooper.TaskData,
                        response: R,
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
                },
            )
        }
        return specificTransactions
    }
}

sealed class SpecificTransactionsUiState {
    data object Loading : SpecificTransactionsUiState()
    data object Error : SpecificTransactionsUiState()
    data class Success(val transactionsList: ArrayList<Transaction>) : SpecificTransactionsUiState()
}
