package org.mifos.mobilewallet.home.domain.usecase;

import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.auth.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;
import org.mifos.mobilewallet.utils.Constants;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 17/6/17.
 */

public class FetchClientData extends UseCase<FetchClientData.RequestValues,
        FetchClientData.ResponseValue> {

    private final LocalRepository localRepository;
    private final FineractRepository fineractRepository;
    private final PreferencesHelper preferencesHelper;

    @Inject
    public FetchClientData(LocalRepository localRepository, FineractRepository fineractRepository, PreferencesHelper preferencesHelper) {
        this.localRepository = localRepository;
        this.fineractRepository = fineractRepository;
        this.preferencesHelper = preferencesHelper;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

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
                        saveClientDetails(clientDetails);
                        getUseCaseCallback().onSuccess(new ResponseValue(clientDetails));
                    }
                });
    }

    private void saveClientDetails(ClientDetails clientDetails) {
        preferencesHelper.saveFullName(clientDetails.getName());
        preferencesHelper.setClientId(clientDetails.getClientId());
    }

    public static final class RequestValues implements UseCase.RequestValues {


        public RequestValues() {

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
