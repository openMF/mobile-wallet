package mifos.org.mobilewallet.core.data.fineract.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import mifos.org.mobilewallet.core.data.fineract.entity.accounts.savings.Transactions;
import mifos.org.mobilewallet.core.domain.model.Transaction;
import mifos.org.mobilewallet.core.injection.PerActivity;
import mifos.org.mobilewallet.core.utils.DateHelper;

/**
 * Created by naman on 15/8/17.
 */

@Singleton
public class TransactionMapper {

    @Inject
    public TransactionMapper() {}

    public List<Transaction> transformTransactionList(SavingsWithAssociations savingsWithAssociations) {
        List<Transaction> transactionList = new ArrayList<>();

        if (savingsWithAssociations != null && savingsWithAssociations.getTransactions() != null
                && savingsWithAssociations.getTransactions().size() != 0) {

            for (Transactions transactions : savingsWithAssociations.getTransactions()) {
                transactionList.add(transformInvoice(transactions));
            }


        }
        return transactionList;
    }

    public Transaction transformInvoice(Transactions transactions) {
        Transaction transaction = new Transaction();

        if (transactions != null ) {

            transaction.setAmount(transactions.getAmount());

            if (transactions.getPaymentDetailData() != null) {
                transaction.setTransactionId(transactions.getPaymentDetailData().getReceiptNumber());
            }

            if (transactions.getSubmittedOnDate() != null) {
                transaction.setDate(DateHelper.getDateAsString(transactions.getSubmittedOnDate()));
            }

        }
        return transaction;
    }
}
