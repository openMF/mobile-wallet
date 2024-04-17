package org.mifospay.core.data.fineract.entity.mapper

import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import com.mifospay.core.model.entity.accounts.savings.Transactions
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.utils.DateHelper
import javax.inject.Inject

class TransactionMapper @Inject constructor(private val currencyMapper: CurrencyMapper) {

    fun transformTransactionList(savingsWithAssociations: SavingsWithAssociations?): List<Transaction> {
        val transactionList = ArrayList<Transaction>()

        savingsWithAssociations?.transactions?.forEach { transaction ->
            transactionList.add(transformInvoice(transaction))
        }
        return transactionList
    }

 fun transformInvoice(transactions: Transactions?): Transaction {
        val transaction = Transaction()

        if (transactions != null) {
            transaction.transactionId = transactions.id.toString()
            transactions.paymentDetailData?.let {
                transaction.receiptId = it.receiptNumber
            }
            transaction.amount = transactions.amount
            transactions.submittedOnDate?.let {
                transaction.date = DateHelper.getDateAsString(it)
            }
            transaction.currency = currencyMapper.transform(transactions.currency)
            transaction.transactionType = TransactionType.OTHER

            if (transactions.transactionType.deposit) {
                transaction.transactionType = TransactionType.CREDIT
            }

            if (transactions.transactionType.withdrawal) {
                transaction.transactionType = TransactionType.DEBIT
            }

            transactions.transfer?.let {
                transaction.transferId = it.id
            }
        }
        return transaction
    }
}
