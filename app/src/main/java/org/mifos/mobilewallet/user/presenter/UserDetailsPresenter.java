package org.mifos.mobilewallet.user.presenter;

import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.core.domain.usecase.FetchClientData;
import org.mifos.mobilewallet.user.UserContract;
import org.mifos.mobilewallet.user.domain.usecase.VerifyPanDetails;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;

/**
 * Created by naman on 22/6/17.
 */

public class UserDetailsPresenter implements UserContract.UserDetailsPresenter {

    private UserContract.UserDetailsView mUserDetailsView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchClientData fetchUserData;

    @Inject
    VerifyPanDetails verifyPanDetails;

    @Inject
    public UserDetailsPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mUserDetailsView = (UserContract.UserDetailsView) baseView;
        mUserDetailsView.setPresenter(this);
    }

    @Override
    public void getUserDetails() {
        mUsecaseHandler.execute(fetchUserData, null,
                new UseCase.UseCaseCallback<FetchClientData.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchClientData.ResponseValue response) {
                        if (!response.getUserDetails().getName().equals(""))
                            mUserDetailsView.showUserDetails(response.getUserDetails());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    @Override
    public void verifyPan(String number) {
        mUsecaseHandler.execute(verifyPanDetails, new VerifyPanDetails.RequestValues(number),
                new UseCase.UseCaseCallback<VerifyPanDetails.ResponseValue>() {
                    @Override
                    public void onSuccess(VerifyPanDetails.ResponseValue response) {
                        mUserDetailsView.showPanStatus(response.isStatus());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
