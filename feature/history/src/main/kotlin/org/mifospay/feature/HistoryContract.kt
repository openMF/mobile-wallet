package org.mifospay.feature

import com.mifospay.core.model.domain.Transaction

/**
 * Created by naman on 17/8/17.
 */
interface HistoryContract {
    interface TransactionsHistoryAsync {
        fun onTransactionsFetchCompleted(transactions: List<Transaction>?)
    }
}