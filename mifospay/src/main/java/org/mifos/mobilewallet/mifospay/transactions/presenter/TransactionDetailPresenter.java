package org.mifos.mobilewallet.mifospay.transactions.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchTransactionReceipt;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by ankur on 06/June/2018
 */

public class TransactionDetailPresenter implements TransactionsContract.TransactionDetailPresenter {

    private final UseCaseHandler mUseCaseHandler;
    @Inject
    FetchTransactionReceipt mFetchTransactionReceiptUseCase;
    private TransactionsContract.TransactionDetailView mTransactionDetailView;

    @Inject
    public TransactionDetailPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransactionDetailView = (TransactionsContract.TransactionDetailView) baseView;
        mTransactionDetailView.setPresenter(this);
    }

    @Override
    public ArrayList<Transaction> getSpecificTransactions(ArrayList<Transaction> transactions,
            String secondAccountNumber) {
        ArrayList<Transaction> specificTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getTransferDetail() != null
                    && (transaction.getTransferDetail().getFromAccount().getAccountNo().equals(
                    secondAccountNumber)
                    || transaction.getTransferDetail().getToAccount().getAccountNo().equals(
                    secondAccountNumber))) {

                specificTransactions.add(transaction);
            }
        }
        return specificTransactions;
    }
}
