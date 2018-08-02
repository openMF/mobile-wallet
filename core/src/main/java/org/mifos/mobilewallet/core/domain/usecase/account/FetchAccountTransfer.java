package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 05/June/2018
 */

public class FetchAccountTransfer extends
        UseCase<FetchAccountTransfer.RequestValues, FetchAccountTransfer.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchAccountTransfer(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.getAccountTransfer(requestValues.transferId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<TransferDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(TransferDetail transferDetail) {
                        getUseCaseCallback().onSuccess(new ResponseValue(transferDetail));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private long transferId;

        public RequestValues(long transferId) {
            this.transferId = transferId;
        }

        public long getTransferId() {
            return transferId;
        }

        public void setTransferId(long transferId) {
            this.transferId = transferId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private TransferDetail transferDetail;

        public ResponseValue(
                TransferDetail transferDetail) {
            this.transferDetail = transferDetail;
        }

        public TransferDetail getTransferDetail() {
            return transferDetail;
        }
    }

}
