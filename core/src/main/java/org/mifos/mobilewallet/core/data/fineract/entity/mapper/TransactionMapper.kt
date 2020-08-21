package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.Transactions
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.model.TransactionType
import org.mifos.mobilewallet.core.utils.DateHelper
import java.util.*
import javax.inject.Inject

/**
 * Created by naman on 15/8/17.
 */
class TransactionMapper @Inject constructor() {

    @Inject
    lateinit var currencyMapper: CurrencyMapper

    fun transformTransactionList(savingsWithAssociations: SavingsWithAssociations?)
            : List<Transaction> {
        var transactionList: MutableList<Transaction> = ArrayList()
        if (savingsWithAssociations?.transactions != null
                && savingsWithAssociations.transactions.isNotEmpty()) {
            for (transactions in savingsWithAssociations.transactions) {
                transactionList.add(transformInvoice(transactions))
            }
        }
        return transactionList
    }

    fun transformInvoice(transactions: Transactions?): Transaction {
        val transaction = Transaction()
        if (transactions != null) {
            transaction.transactionId = transactions.id.toString()
            if (transactions.paymentDetailData != null && transactions.paymentDetailData != null) {
                transaction.receiptId = transactions.paymentDetailData!!.receiptNumber
            }
            transaction.amount = transactions.amount!!
            if (transactions.submittedOnDate != null) {
                transaction.date = DateHelper.getDateAsString(transactions.submittedOnDate)
            }
            transaction.currency = currencyMapper!!.transform(transactions.currency!!)
            transaction.transactionType = TransactionType.OTHER
            if (transactions.transactionType!!.deposit!!) {
                transaction.transactionType = TransactionType.CREDIT
            }
            if (transactions.transactionType!!.withdrawal!!) {
                transaction.transactionType = TransactionType.DEBIT
            }
            if (transactions.transfer != null) {
                transaction.transferId = transactions.transfer!!.id
            }
        }
        return transaction
    }
}