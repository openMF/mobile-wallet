package org.mifos.mobilewallet.core.domain.usecase.kyc;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 24/May/2018
 */

public class UploadKYCLevel1Details extends
        UseCase<UploadKYCLevel1Details.RequestValues, UploadKYCLevel1Details.ResponseValue> {

    FineractRepository mFineractRepository;

    @Inject
    public UploadKYCLevel1Details(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.uploadKYCLevel1Details(requestValues.clientId,
                requestValues.mKYCLevel1Details)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });

    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final int clientId;
        private final KYCLevel1Details mKYCLevel1Details;

        public RequestValues(int clientId,
                KYCLevel1Details kycLevel1Details) {
            this.clientId = clientId;
            mKYCLevel1Details = kycLevel1Details;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
