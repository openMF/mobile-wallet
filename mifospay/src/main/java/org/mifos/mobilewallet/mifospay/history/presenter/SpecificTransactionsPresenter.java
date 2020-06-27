package org.mifos.mobilewallet.mifospay.history.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;

import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by ankur on 08/June/2018
 */

public class SpecificTransactionsPresenter implements
        HistoryContract.SpecificTransactionsPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private HistoryContract.SpecificTransactionsView mSpecificTransactionsView;

    @Inject
    public SpecificTransactionsPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mSpecificTransactionsView = (HistoryContract.SpecificTransactionsView) baseView;
        mSpecificTransactionsView.setPresenter(this);
    }

    @Override
    public void getSpecificTransactions(
            final ArrayList<JournalEntry> transactions, final String secondAccountIdentifier) {
        final ArrayList<JournalEntry> specificTransactions = new ArrayList<>();
        mSpecificTransactionsView.showProgress();
        if (transactions != null && transactions.size() > 0) {

            for (JournalEntry transaction : transactions) {
                boolean isCreditor =
                        transaction.getCreditors().get(0).getAccountNumber().
                                equals(secondAccountIdentifier);
                boolean isDebtor =
                        transaction.getDebtors().get(0).getAccountNumber().
                                equals(secondAccountIdentifier);
                if (isCreditor || isDebtor) {
                    specificTransactions.add(transaction);
                }
            }

            if (specificTransactions.size() == 0) {
                mSpecificTransactionsView.showStateView(
                        R.drawable.ic_empty_state,
                        R.string.error_oops,
                        R.string.empty_specific_transactions);
            } else {
                mSpecificTransactionsView.showSpecificTransactions(specificTransactions);
            }
        } else {
            mSpecificTransactionsView.showStateView(
                    R.drawable.ic_error_state,
                    R.string.error_oops,
                    R.string.unexpected_error_subtitle);
        }

    }
}
