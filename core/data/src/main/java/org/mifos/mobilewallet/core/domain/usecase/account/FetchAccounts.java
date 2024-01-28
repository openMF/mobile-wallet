package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.AccountMapper;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.List;

import javax.inject.Inject;

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
    AccountMapper accountMapper;

    @Inject
    public FetchAccounts(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        fineractRepository.getAccounts(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAccounts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_ACCOUNTS);
                    }

                    @Override
                    public void onNext(ClientAccounts accounts) {
                        if (accounts != null) {
                            getUseCaseCallback().onSuccess(new
                                    ResponseValue(accountMapper.transform(accounts)));
                        } else {
                            getUseCaseCallback().onError(Constants.NO_ACCOUNTS_FOUND);
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

