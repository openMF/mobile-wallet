package org.mifos.mobilewallet.core.domain.usecase.client;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.client.NewClient;
import org.mifos.mobilewallet.core.utils.ErrorJsonMessageHelper;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 20/8/17.
 */

public class CreateClient extends UseCase<CreateClient.RequestValues, CreateClient.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    public CreateClient(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        apiRepository.createClient(requestValues.client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CreateClient.ResponseValue>() {
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
                    public void onNext(CreateClient.ResponseValue genericResponse) {
                        getUseCaseCallback().onSuccess(genericResponse);
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final NewClient client;

        public RequestValues(NewClient client) {
            this.client = client;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final int clientId;

        public ResponseValue(int clientId) {
            this.clientId = clientId;
        }

        public int getClientId() {
            return clientId;
        }
    }

}
