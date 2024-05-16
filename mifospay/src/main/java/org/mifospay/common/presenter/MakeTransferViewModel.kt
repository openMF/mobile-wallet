package org.mifospay.common.presenter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.TransferFunds
import org.mifospay.core.data.domain.usecase.client.SearchClient
import org.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class MakeTransferViewModel @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val searchClientUseCase: SearchClient,
    private val transferFundsUseCase: TransferFunds,
    private val localRepository: LocalRepository?
) : ViewModel() {

    private val _makeTransferState = MutableStateFlow<MakeTransferState>(MakeTransferState.Loading)
    val makeTransferState: StateFlow<MakeTransferState> = _makeTransferState.asStateFlow()

    private val _showTransactionStatus = MutableStateFlow(
        ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false
        )
    )
    val showTransactionStatus: StateFlow<ShowTransactionStatus> =
        _showTransactionStatus.asStateFlow()

    fun fetchClient(externalId: String, transferAmount: Double) {
        mUsecaseHandler.execute(searchClientUseCase, SearchClient.RequestValues(externalId),
            object : UseCase.UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    val searchResult = response.results[0]
                    searchResult.resultId.let {
                        _makeTransferState.value = MakeTransferState.Success(
                            it.toLong(),
                            searchResult.resultName,
                            externalId,
                            transferAmount,
                            true
                        )
                    }
                }

                override fun onError(message: String) {
                    _makeTransferState.value = MakeTransferState.Error(message)
                }
            })
    }

    fun makeTransfer(toClientId: Long, amount: Double) {
        val fromClientId = localRepository?.clientDetails?.clientId ?: 0
        mUsecaseHandler.execute(transferFundsUseCase,
            TransferFunds.RequestValues(fromClientId, toClientId, amount),
            object : UseCase.UseCaseCallback<TransferFunds.ResponseValue> {
                override fun onSuccess(response: TransferFunds.ResponseValue) {

                    _showTransactionStatus.value = ShowTransactionStatus(
                        showSuccessStatus = true,
                        showErrorStatus = false
                    )
                }

                override fun onError(message: String) {
                    _showTransactionStatus.value = ShowTransactionStatus(
                        showSuccessStatus = false,
                        showErrorStatus = true
                    )
                }
            })
    }
}

data class ShowTransactionStatus(
    val showSuccessStatus: Boolean,
    val showErrorStatus: Boolean
)

sealed interface MakeTransferState {
    data object Loading : MakeTransferState
    data class Success(
        val toClientId: Long,
        val resultName: String,
        val externalId: String,
        val transferAmount: Double,
        val showBottomSheet: Boolean,
    ) : MakeTransferState

    data class Error(val message: String) : MakeTransferState
}