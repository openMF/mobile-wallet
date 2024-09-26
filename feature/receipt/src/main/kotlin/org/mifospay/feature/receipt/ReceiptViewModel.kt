/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.receipt

import android.net.Uri
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ResponseBody
import org.mifospay.core.common.Constants
import org.mifospay.common.FileUtils
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.DownloadTransactionReceipt
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransaction
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.datastore.PreferencesHelper
import java.io.File

class ReceiptViewModel(
    private val mUseCaseHandler: UseCaseHandler,
    private val preferencesHelper: PreferencesHelper,
    private val downloadTransactionReceiptUseCase: DownloadTransactionReceipt,
    private val fetchAccountTransactionUseCase: FetchAccountTransaction,
    private val fetchAccountTransferUseCase: FetchAccountTransfer,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val mReceiptState = MutableStateFlow<ReceiptUiState>(ReceiptUiState.Loading)
    val receiptUiState: StateFlow<ReceiptUiState> = mReceiptState.asStateFlow()

    private val mFileState = MutableStateFlow(PassFileState())
    val fileState: StateFlow<PassFileState> = mFileState.asStateFlow()

    init {
        savedStateHandle.get<String>("uri")?.let {
            getTransactionData(Uri.parse(it))
        }
    }

    fun downloadReceipt(transactionId: String?) {
        mUseCaseHandler.execute(
            downloadTransactionReceiptUseCase,
            DownloadTransactionReceipt.RequestValues(transactionId),
            object : UseCase.UseCaseCallback<DownloadTransactionReceipt.ResponseValue> {
                override fun onSuccess(response: DownloadTransactionReceipt.ResponseValue) {
                    val filename = Constants.RECEIPT + transactionId + Constants.PDF
                    writeReceiptToPDF(response.responseBody, filename)
                }

                override fun onError(message: String) {
                    mReceiptState.value = ReceiptUiState.Error(message)
                }
            },
        )
    }

    fun writeReceiptToPDF(responseBody: ResponseBody?, filename: String) {
        val mifosDirectory = File(
            Environment.getExternalStorageDirectory(),
            Constants.MIFOSPAY,
        )
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs()
        }
        val documentFile = File(mifosDirectory.path, filename)
        if (FileUtils.writeInputStreamDataToFile(responseBody!!.byteStream(), documentFile)) {
            mFileState.value = PassFileState(documentFile)
        }
    }

    private fun getTransactionData(data: Uri?) {
        if (data != null) {
            val params = data.pathSegments
            val transactionId = params.getOrNull(0)
            val receiptLink = data.toString()
            fetchTransaction(transactionId, receiptLink)
        }
    }

    private fun fetchTransaction(transactionId: String?, receiptLink: String?) {
        val accountId = preferencesHelper.accountId

        if (transactionId != null) {
            mUseCaseHandler.execute(
                fetchAccountTransactionUseCase,
                FetchAccountTransaction.RequestValues(accountId, transactionId.toLong()),
                object : UseCase.UseCaseCallback<FetchAccountTransaction.ResponseValue> {
                    override fun onSuccess(response: FetchAccountTransaction.ResponseValue) {
                        if (receiptLink != null) {
                            fetchTransfer(
                                response.transaction,
                                response.transaction.transferId,
                                receiptLink,
                            )
                        }
                    }

                    override fun onError(message: String) {
                        if (message == Constants.UNAUTHORIZED_ERROR) {
                            mReceiptState.value = ReceiptUiState.OpenPassCodeActivity
                        } else {
                            mReceiptState.value = ReceiptUiState.Error(message)
                        }
                    }
                },
            )
        }
    }

    fun fetchTransfer(
        transaction: Transaction,
        transferId: Long,
        receiptLink: String,
    ) {
        mUseCaseHandler.execute(
            fetchAccountTransferUseCase,
            FetchAccountTransfer.RequestValues(transferId),
            object : UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransfer.ResponseValue?) {
                    if (response != null) {
                        mReceiptState.value =
                            ReceiptUiState.Success(
                                transaction,
                                response.transferDetail,
                                receiptLink,
                            )
                    }
                }

                override fun onError(message: String) {
                    mReceiptState.value = ReceiptUiState.Error(message)
                }
            },
        )
    }
}

data class PassFileState(
    val file: File = File(""),
)

sealed interface ReceiptUiState {
    data class Success(
        val transaction: Transaction,
        val transferDetail: TransferDetail,
        val receiptLink: String,
    ) : ReceiptUiState

    data object OpenPassCodeActivity : ReceiptUiState
    data class Error(
        val message: String,
    ) : ReceiptUiState

    data object Loading : ReceiptUiState
}
