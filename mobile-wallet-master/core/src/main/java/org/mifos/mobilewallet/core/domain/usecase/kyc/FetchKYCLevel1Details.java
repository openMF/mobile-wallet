package org.mifos.mobilewallet.core.domain.usecase.kyc;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 24/May/2018
 */

public class FetchKYCLevel1Details extends
        UseCase<FetchKYCLevel1Details.RequestValues, FetchKYCLevel1Details.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchKYCLevel1Details(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.fetchKYCLevel1Details(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<KYCLevel1Details>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(List<KYCLevel1Details> kycLevel1Details) {
                        getUseCaseCallback().onSuccess(
                                new ResponseValue(kycLevel1Details));
                    }
                });


    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final int clientId;

        public RequestValues(int clientId) {
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<KYCLevel1Details> mKYCLevel1DetailsList;

        public ResponseValue(
                List<KYCLevel1Details> kycLevel1Details) {
            mKYCLevel1DetailsList = kycLevel1Details;
        }

        public List<KYCLevel1Details> getKYCLevel1DetailsList() {
            return mKYCLevel1DetailsList;
        }
    }
}
