package org.mifos.mobilewallet.core.data.paymenthub.repository


import javax.inject.Inject
import javax.inject.Singleton

import org.mifos.mobilewallet.core.data.paymenthub.api.PaymentHubApiManager
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction

/**
 * Created by naman on 6/4/19.
 */

@Singleton
class PaymentHubRepository @Inject
constructor(private val paymentHubApiManager: PaymentHubApiManager) {

    fun createTransaction(transaction: Transaction) =
            paymentHubApiManager.transactionsApi.createPaymentRequest(transaction)

    fun fetchTransactionInfo(transactionId: String) =
            paymentHubApiManager.transactionsApi.fetchTransactionInfo(transactionId)

}
