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
 * Created by ankur on 25/May/2018
 */

public class UpdateKYCLevel1Details extends
        UseCase<UpdateKYCLevel1Details.RequestValues, UpdateKYCLevel1Details.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public UpdateKYCLevel1Details(FineractRepository fineractRepository) {
        this.mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.updateKYCLevel1Details(requestValues.clientId,
                requestValues.kycLevel1Details)
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
        private final KYCLevel1Details kycLevel1Details;

        public RequestValues(int clientId,
                KYCLevel1Details kycLevel1Details) {
            this.clientId = clientId;
            this.kycLevel1Details = kycLevel1Details;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
