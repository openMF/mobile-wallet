package org.mifos.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.UpdateVpaPayload;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 19/8/17.
 */

public class UpdateVpa extends UseCase<UpdateVpa.RequestValues,
        UpdateVpa.ResponseValue> {

    private final FineractRepository fineractRepository;

    @Inject
    public UpdateVpa(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        UpdateVpaPayload vpaPayload = new UpdateVpaPayload();
        vpaPayload.setExternalId(requestValues.vpa);
        fineractRepository.updateClientVpa(requestValues.clientId, vpaPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error updating vpa");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final String vpa;
        private final long clientId;

        public RequestValues(long clientId, String vpa) {
            this.clientId = clientId;
            this.vpa = vpa;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        public ResponseValue() {}

    }
}