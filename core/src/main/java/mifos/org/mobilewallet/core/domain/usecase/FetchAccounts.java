package mifos.org.mobilewallet.core.domain.usecase;

import java.util.List;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.domain.model.Account;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 11/7/17.
 */

public class FetchAccounts extends UseCase<FetchAccounts.RequestValues,
        FetchAccounts.ResponseValue> {

    private final FineractRepository fineractRepository;

    @Inject
    public FetchAccounts(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(FetchAccounts.RequestValues requestValues) {

        fineractRepository.getAccounts(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Account>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error fetching accounts");
                    }

                    @Override
                    public void onNext(List<Account> accounts) {
                        if (accounts != null && accounts.size() != 0) {
                            getUseCaseCallback().onSuccess(new FetchAccounts.ResponseValue(accounts));
                        } else {
                            getUseCaseCallback().onError("No accounts found");
                        }
                    }
                });
    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final long clientId;

        public RequestValues(long clientId) {
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Account> accountList;

        public ResponseValue(List<Account> accounts) {
            this.accountList = accounts;
        }

        public List<Account> getAccountList() {
            return accountList;
        }
    }
}

