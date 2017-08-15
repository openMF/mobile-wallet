package mifos.org.mobilewallet.core.domain.usecase;

import java.util.List;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.data.local.LocalRepository;
import mifos.org.mobilewallet.core.domain.model.Transaction;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 11/7/17.
 */

public class FetchAccountTransactions extends UseCase<FetchAccountTransactions.RequestValues,
        FetchAccountTransactions.ResponseValue> {

    private final LocalRepository localRepository;
    private final FineractRepository fineractRepository;

    @Inject
    public FetchAccountTransactions(LocalRepository localRepository, FineractRepository fineractRepository) {
        this.localRepository = localRepository;
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(final FetchAccountTransactions.RequestValues requestValues) {

        fineractRepository.getAccountTransactions(requestValues.accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Transaction>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error fetching remote account transactions");
                    }

                    @Override
                    public void onNext(List<Transaction> transactions) {
                        getUseCaseCallback().onSuccess(new ResponseValue(transactions));
                    }
                });



    }

    public static final class RequestValues implements UseCase.RequestValues {

        private long accountId;

        public RequestValues(long accountId) {
            this.accountId = accountId;
        }

        public void setAccountId(long accountId) {
            this.accountId = accountId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private List<Transaction> transactions;

        public ResponseValue(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }
    }

}


