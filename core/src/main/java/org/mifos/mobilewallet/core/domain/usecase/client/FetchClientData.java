package org.mifos.mobilewallet.core.domain.usecase.client;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.Page;
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.ClientDetailsMapper;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;

import javax.inject.Inject;

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
    ClientDetailsMapper clientDetailsMapper;

    @Inject
    public FetchClientData(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        if (requestValues != null) {
            fineractRepository.getSelfClientDetails(requestValues.clientid)
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
                            getUseCaseCallback().onSuccess(new
                                    ResponseValue(clientDetailsMapper.transform(client)));
                        }
                    });
        } else {
            fineractRepository.getSelfClientDetails()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Page<Client>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getUseCaseCallback().onError(Constants.ERROR_FETCHING_CLIENT_DATA);
                        }

                        @Override
                        public void onNext(Page<Client> client) {
                            if (client != null && client.getPageItems() != null
                                    && client.getPageItems().size() != 0) {
                                getUseCaseCallback().onSuccess(new
                                        ResponseValue(clientDetailsMapper
                                        .transform(client.getPageItems().get(0))));
                            } else {
                                getUseCaseCallback().onError(Constants.NO_CLIENT_FOUND);
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

        private final org.mifos.mobilewallet.core.domain.model.client.Client user;

        public ResponseValue(org.mifos.mobilewallet.core.domain.model.client.Client user) {
            this.user = user;
        }

        public org.mifos.mobilewallet.core.domain.model.client.Client getUserDetails() {
            return user;
        }
    }
}
