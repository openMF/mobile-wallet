package mifos.org.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.domain.model.ClientDetails;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 17/6/17.
 */

public class FetchClientData extends UseCase<FetchClientData.RequestValues,
        FetchClientData.ResponseValue> {

    private final FineractRepository fineractRepository;

    @Inject
    public FetchClientData(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        if (requestValues != null) {
            fineractRepository.getClientDetails(requestValues.clientid)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<ClientDetails>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getUseCaseCallback().onError("Error fetching client data");
                        }

                        @Override
                        public void onNext(ClientDetails clientDetails) {
                            getUseCaseCallback().onSuccess(new ResponseValue(clientDetails));
                        }
                    });
        } else {
            fineractRepository.getClientDetails()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<ClientDetails>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getUseCaseCallback().onError("Error fetching client data");
                        }

                        @Override
                        public void onNext(ClientDetails clientDetails) {
                            if (clientDetails != null) {
                                getUseCaseCallback().onSuccess(new ResponseValue(clientDetails));
                            } else {
                                getUseCaseCallback().onError("No client found");
                            }
                        }
                    });
        }
    }



    public static final class RequestValues implements UseCase.RequestValues {

        private final long clientid;

        public RequestValues(long clientid) {
            this.clientid = clientid;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final ClientDetails user;

        public ResponseValue(ClientDetails user) {
            this.user = user;
        }

        public ClientDetails getUserDetails() {
            return user;
        }
    }
}
