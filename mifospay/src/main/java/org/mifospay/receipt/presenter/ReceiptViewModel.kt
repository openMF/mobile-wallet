package org.mifospay.receipt.presenter


import android.os.Environment
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.ResponseBody
import org.mifospay.common.Constants
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.DownloadTransactionReceipt
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransaction
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.utils.FileUtils
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val preferencesHelper: PreferencesHelper,
    private val downloadTransactionReceiptUseCase: DownloadTransactionReceipt,
    private val fetchAccountTransactionUseCase: FetchAccountTransaction,
    private val fetchAccountTransferUseCase: FetchAccountTransfer,
) : ViewModel() {
    private val _receiptState = MutableStateFlow<ReceiptUiState>(ReceiptUiState.Loading)
    val receiptUiState: StateFlow<ReceiptUiState> = _receiptState.asStateFlow()

    private val _fileState = MutableStateFlow(PassFileState())
    val fileState: StateFlow<PassFileState> = _fileState.asStateFlow()

    fun downloadReceipt(transactionId: String?) {
        mUseCaseHandler.execute(downloadTransactionReceiptUseCase,
            DownloadTransactionReceipt.RequestValues(transactionId),
            object : UseCase.UseCaseCallback<DownloadTransactionReceipt.ResponseValue> {
                override fun onSuccess(response: DownloadTransactionReceipt.ResponseValue) {
                    val filename = Constants.RECEIPT + transactionId + Constants.PDF
                    writeReceiptToPDF(response.responseBody, filename)
                }

                override fun onError(message: String) {
                    _receiptState.value = ReceiptUiState.Error(message)
                }
            })
    }

    fun writeReceiptToPDF(responseBody: ResponseBody?, filename: String) {
        val mifosDirectory = File(
            Environment.getExternalStorageDirectory(),
            Constants.MIFOSPAY
        )
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs()
        }
        val documentFile = File(mifosDirectory.path, filename)
        if (FileUtils.writeInputStreamDataToFile(responseBody!!.byteStream(), documentFile)) {
            _fileState.value = PassFileState(documentFile, true)
        } else {
            _fileState.update { currentState ->
                currentState.copy(writeReceiptToPDFisSuccess = false)
            }
        }
    }

    fun fetchTransaction(transactionId: String?) {
        val accountId = preferencesHelper.accountId

        if (transactionId != null) {
            mUseCaseHandler.execute(fetchAccountTransactionUseCase,
                FetchAccountTransaction.RequestValues(accountId, transactionId.toLong()),
                object : UseCase.UseCaseCallback<FetchAccountTransaction.ResponseValue> {
                    override fun onSuccess(response: FetchAccountTransaction.ResponseValue) {
                        _receiptState.value = ReceiptUiState.Success(response.transaction)
                        fetchTransfer(response.transaction.transferId)
                    }

                    override fun onError(message: String) {
                        if (message == Constants.UNAUTHORIZED_ERROR) {
                            _receiptState.value = ReceiptUiState.OpenPassCodeActivity
                        } else {
                            _receiptState.value = ReceiptUiState.Error(message)
                        }
                    }
                }
            )
        }
    }

    fun fetchTransfer(transferId: Long) {
        mUseCaseHandler.execute(fetchAccountTransferUseCase,
            FetchAccountTransfer.RequestValues(transferId),
            object : UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransfer.ResponseValue?) {}

                override fun onError(message: String) {
                    _receiptState.value = ReceiptUiState.Error(message)
                }
            })
    }
}

data class PassFileState(
    val file: File = File(" "),
    val writeReceiptToPDFisSuccess: Boolean = false
)

sealed interface ReceiptUiState {
    data class Success(
        val transaction: Transaction
    ) : ReceiptUiState

    data object OpenPassCodeActivity : ReceiptUiState
    data class Error(
        val message: String
    ) : ReceiptUiState

    data object Loading : ReceiptUiState
}
