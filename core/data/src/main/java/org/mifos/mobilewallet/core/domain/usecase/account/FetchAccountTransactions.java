package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.TransactionMapper;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 11/7/17.
 */

public class FetchAccountTransactions extends UseCase<FetchAccountTransactions.RequestValues,
        FetchAccountTransactions.ResponseValue> {

    private final FineractRepository fineractRepository;

    @Inject
    TransactionMapper transactionMapper;


    @Inject
    public FetchAccountTransactions(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(final RequestValues requestValues) {

        fineractRepository.getSelfAccountTransactions(requestValues.accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(
                                Constants.ERROR_FETCHING_REMOTE_ACCOUNT_TRANSACTIONS);
                    }

                    @Override
                    public void onNext(SavingsWithAssociations transactions) {
                        getUseCaseCallback().onSuccess(new
                                ResponseValue(transactionMapper
                                .transformTransactionList(transactions)));
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


