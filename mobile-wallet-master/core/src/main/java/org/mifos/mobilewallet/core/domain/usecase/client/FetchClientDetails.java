package org.mifos.mobilewallet.core.domain.usecase.client;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 24/July/2018
 */

public class FetchClientDetails extends
        UseCase<FetchClientDetails.RequestValues, FetchClientDetails.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchClientDetails(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.getClientDetails(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_CLIENT_DATA);
                    }

                    @Override
                    public void onNext(Client client) {
                        getUseCaseCallback().onSuccess(new ResponseValue(client));
                    }
                });

    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final long clientId;

        public RequestValues(long clientId) {
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private Client mClient;

        public ResponseValue(Client client) {
            mClient = client;
        }

        public Client getClient() {
            return mClient;
        }
    }
}
