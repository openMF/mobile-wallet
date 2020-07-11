package org.mifos.mobilewallet.core.data.paymenthub.repository


import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

import org.mifos.mobilewallet.core.data.paymenthub.api.PaymentHubApiManager
import org.mifos.mobilewallet.core.data.paymenthub.entity.RegistrationEntity
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionResponse
import rx.Observable

/**
 * Created by naman on 6/4/19.
 */

@Singleton
class PaymentHubRepository @Inject constructor(
        private val paymentHubApiManager: PaymentHubApiManager) {

    fun createTransaction(transaction: Transaction): Observable<TransactionInfo> =
            paymentHubApiManager.transactionsApi.makeTransaction(transaction)

    fun fetchTransactionInfo(transactionId: String): Observable<TransactionResponse> =
            paymentHubApiManager.transactionsApi.fetchTransactionInfo(transactionId)

    fun registerSecondaryIdentifier(registrationEntity: RegistrationEntity):
            Observable<ResponseBody> =
            paymentHubApiManager.registrationApi.registerSecondaryIdentifier(registrationEntity)

    fun requestTransaction(transactionRequest: Transaction): Observable<TransactionInfo> =
            paymentHubApiManager.transactionsApi.requestTransaction(transactionRequest)

}
