package org.mifospay.feature.receipt


import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ResponseBody
import org.mifospay.common.Constants
import org.mifospay.common.FileUtils
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.DownloadTransactionReceipt
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransaction
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.datastore.PreferencesHelper
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
            _fileState.value = PassFileState(documentFile)
        }
    }

    fun getTransactionData(data: Uri?) {
        if (data != null) {
            val scheme = data.scheme // "https"
            val host = data.host // "receipt.mifospay.com"
            val params = data.pathSegments
            val transactionId = params.getOrNull(0)
            val receiptLink = data.toString()
            fetchTransaction(transactionId, receiptLink)
        }
    }

    fun fetchTransaction(transactionId: String?, receiptLink: String?) {
        val accountId = preferencesHelper.accountId

        if (transactionId != null) {
            mUseCaseHandler.execute(fetchAccountTransactionUseCase,
                FetchAccountTransaction.RequestValues(accountId, transactionId.toLong()),
                object : UseCase.UseCaseCallback<FetchAccountTransaction.ResponseValue> {
                    override fun onSuccess(response: FetchAccountTransaction.ResponseValue) {
                        if (receiptLink != null) {
                            fetchTransfer(
                                response.transaction,
                                response.transaction.transferId,
                                receiptLink
                            )
                        }
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

    fun fetchTransfer(
        transaction: Transaction,
        transferId: Long,
        receiptLink: String
    ) {
        mUseCaseHandler.execute(fetchAccountTransferUseCase,
            FetchAccountTransfer.RequestValues(transferId),
            object : UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransfer.ResponseValue?) {
                    if (response != null) {
                        _receiptState.value =
                            ReceiptUiState.Success(
                                transaction,
                                response.transferDetail,
                                receiptLink
                            )
                    }
                }

                override fun onError(message: String) {
                    _receiptState.value = ReceiptUiState.Error(message)
                }
            })
    }

}

data class PassFileState(
    val file: File = File("")
)

sealed interface ReceiptUiState {
    data class Success(
        val transaction: Transaction,
        val transferDetail: TransferDetail,
        val receiptLink: String
    ) : ReceiptUiState

    data object OpenPassCodeActivity : ReceiptUiState
    data class Error(
        val message: String
    ) : ReceiptUiState

    data object Loading : ReceiptUiState
}
