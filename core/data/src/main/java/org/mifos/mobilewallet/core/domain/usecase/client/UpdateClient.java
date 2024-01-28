package org.mifos.mobilewallet.core.domain.usecase.client;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.ErrorJsonMessageHelper;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 19/8/17.
 */

public class UpdateClient extends UseCase<UpdateClient.RequestValues,
        UpdateClient.ResponseValue> {

    private final FineractRepository fineractRepository;

    @Inject
    public UpdateClient(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        fineractRepository.updateClient(requestValues.clientId, requestValues.updateClientEntity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String message = "Error";
                        try {
                            message = ((HttpException) e).response().errorBody().string();
                            message = ErrorJsonMessageHelper.getUserMessage(message);
                        } catch (Exception e1) {
                            message = "Error";
                        }
                        getUseCaseCallback().onError(message);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final Object updateClientEntity;
        private final long clientId;

        public RequestValues(Object updateClientEntity, long clientId) {
            this.updateClientEntity = updateClientEntity;
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        public ResponseValue() {
        }

    }
}