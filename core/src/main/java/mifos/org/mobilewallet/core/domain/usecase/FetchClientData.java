package mifos.org.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.data.local.LocalRepository;
import mifos.org.mobilewallet.core.data.local.PreferencesHelper;
import mifos.org.mobilewallet.core.domain.model.ClientDetails;
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
