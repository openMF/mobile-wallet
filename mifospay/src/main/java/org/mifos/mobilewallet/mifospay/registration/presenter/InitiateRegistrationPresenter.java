package org.mifos.mobilewallet.mifospay.registration.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;

import javax.inject.Inject;

public class InitiateRegistrationPresenter implements
        RegistrationContract.InitiateRegistrationPresenter {
    private final UseCaseHandler mUseCaseHandler;
    RegistrationContract.InitiateRegistrationView mInitiateRegistrationView;
    @Inject
    SearchClient searchClientUseCase;

    @Inject
    public InitiateRegistrationPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void requestOtpFromServer(String fullNumber, String mobileNo) {
        mUseCaseHandler.execute(searchClientUseCase, new SearchClient.RequestValues(mobileNo),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
                    @Override
                    public void onSuccess(SearchClient.ResponseValue response) {
                        mInitiateRegistrationView.onRequestOtpFailed(
                                "Mobile number already exists.");
                    }

                    @Override
                    public void onError(String message) {
                        // TODO:: request OTP
                        mInitiateRegistrationView.onRequestOtpSuccess();
                    }
                });
    }

    @Override
    public void attachView(BaseView baseView) {
        mInitiateRegistrationView = (RegistrationContract.InitiateRegistrationView) baseView;
        mInitiateRegistrationView.setPresenter(this);
    }
}
