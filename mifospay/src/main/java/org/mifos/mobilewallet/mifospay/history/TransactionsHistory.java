package org.mifos.mobilewallet.mifospay.history;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.core.domain.usecase.journal.FetchJournalEntries;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class TransactionsHistory {

    private final UseCaseHandler mUsecaseHandler;
    public HistoryContract.TransactionsHistoryAsync delegate;
    @Inject
    FetchJournalEntries fetchJournalEntriesUseCase;
    @Inject
    UseCaseFactory mUseCaseFactory;
    private List<JournalEntry> transactions;

    @Inject
    public TransactionsHistory(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
        transactions = new ArrayList<>();
    }

    public void fetchTransactionsHistory(String accountIdentifier) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        String dateRange = Constants.STARTING_DATE + ".." + currentDate + "Z";
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
