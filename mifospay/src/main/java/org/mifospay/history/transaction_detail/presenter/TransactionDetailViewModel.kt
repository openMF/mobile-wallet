package org.mifospay.history.transaction_detail.presenter

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mifospay.common.Constants
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mFetchAccountTransferUseCase: FetchAccountTransfer
): ViewModel() {

    private val _transactionDetailUiState: MutableStateFlow<TransactionDetailUiState> = MutableStateFlow(TransactionDetailUiState.Loading)
    val transactionDetailUiState get() = _transactionDetailUiState

    fun getTransferDetail(transferId: Long) {
        mUseCaseHandler.execute(mFetchAccountTransferUseCase,
            FetchAccountTransfer.RequestValues(transferId),
            object : UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransfer.ResponseValue?) {
                    _transactionDetailUiState.value = TransactionDetailUiState.Success(response?.transferDetail)
                }
                override fun onError(message: String) {
                    _transactionDetailUiState.value = TransactionDetailUiState.Error
                }
            }
        )
    }
}

sealed class TransactionDetailUiState {
    data object Loading: TransactionDetailUiState()
    data object Error: TransactionDetailUiState()
    data class Success(val transferDetail: TransferDetail?): TransactionDetailUiState()
}