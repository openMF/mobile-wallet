package org.mifos.mobilewallet.mifospay.history;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.core.domain.usecase.journal.FetchJournalEntries;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TransactionsHistory {

    private final UseCaseHandler mUsecaseHandler;
    public HistoryContract.TransactionsHistoryAsync delegate;
    @Inject
    FetchJournalEntries fetchJournalEntriesUseCase;
    @Inject
    TaskLooper mTaskLooper;
    @Inject
    UseCaseFactory mUseCaseFactory;
    private List<JournalEntry> transactions;

    @Inject
    public TransactionsHistory(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
        transactions = new ArrayList<>();
    }

    public void fetchTransactionsHistory(String accountIdentifier) {
        String dateRange = "";
        mUsecaseHandler.execute(fetchJournalEntriesUseCase,
                new FetchJournalEntries.RequestValues(accountIdentifier, dateRange),
                new UseCase.UseCaseCallback<FetchJournalEntries.ResponseValue>() {

                    @Override
                    public void onSuccess(FetchJournalEntries.ResponseValue response) {
                        transactions = response.getJournalEntryList();
                        delegate.onTransactionsFetchCompleted(transactions);
                    }

                    @Override
                    public void onError(String message) {
                        transactions = null;
                    }
                });
    }
}
