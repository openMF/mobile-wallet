package org.mifos.mobilewallet.core.domain.usecase.twofactor;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.twofactor.DeliveryMethod;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 01/June/2018
 */

public class FetchDeliveryMethods extends
        UseCase<FetchDeliveryMethods.RequestValues, FetchDeliveryMethods.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchDeliveryMethods(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.getDeliveryMethods()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DeliveryMethod>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(List<DeliveryMethod> deliveryMethods) {
                        getUseCaseCallback().onSuccess(new ResponseValue(deliveryMethods));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        public RequestValues() {

        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<DeliveryMethod> deliveryMethodList;

        public ResponseValue(
                List<DeliveryMethod> deliveryMethodList) {
            this.deliveryMethodList = deliveryMethodList;
        }

        public List<DeliveryMethod> getDeliveryMethodList() {
            return deliveryMethodList;
        }
    }
}
