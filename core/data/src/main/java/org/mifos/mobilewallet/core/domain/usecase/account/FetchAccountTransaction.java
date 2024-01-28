package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.Transactions;
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.TransactionMapper;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.utils.Constants;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Shivansh on 15/6/19.
 */

public class FetchAccountTransaction extends UseCase<FetchAccountTransaction.RequestValues,
        FetchAccountTransaction.ResponseValue> {

    private final FineractRepository fineractRepository;

    @Inject
    TransactionMapper transactionMapper;


    @Inject
    public FetchAccountTransaction(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(final FetchAccountTransaction.RequestValues requestValues) {

        fineractRepository.getSelfAccountTransactionFromId(requestValues.accountId,
                requestValues.transactionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Transactions>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().equals("HTTP 401 Unauthorized")) {
                            getUseCaseCallback().onError(Constants.UNAUTHORIZED_ERROR);
                        } else {
                            getUseCaseCallback().onError(
                                    Constants.ERROR_FETCHING_REMOTE_ACCOUNT_TRANSACTIONS);
                        }
                    }

                    @Override
                    public void onNext(Transactions transaction) {
                        getUseCaseCallback().onSuccess(new
                                FetchAccountTransaction.ResponseValue(transactionMapper
                                .transformInvoice(transaction)));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private long transactionId;
        private long accountId;

        public RequestValues(long accountId, long transactionId) {
            this.transactionId = transactionId;
            this.accountId = accountId;
        }

        public void setAccountId(long accountId) {
            this.accountId = accountId;
        }

        public void setTransactionId(long transactionId) {
            this.transactionId = transactionId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private Transaction transaction;

        public ResponseValue(Transaction transaction) {
            this.transaction = transaction;
        }

        public Transaction getTransaction() {
            return transaction;
        }
    }
}
