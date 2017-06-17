package org.mifos.mobilewallet.home;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.core.BasePresenter;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.core.UseCaseHandler;
import org.mifos.mobilewallet.home.domain.usecase.FetchUserData;

import javax.inject.Inject;

/**
 * Created by naman on 17/6/17.
 */

public class HomePresenter implements HomeContract.HomePresenter {

    private HomeContract.HomeView mHomeView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchUserData fetchUserData;

    @Inject
    public HomePresenter(UseCaseHandler useCaseHandler){
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mHomeView = (HomeContract.HomeView) baseView;
        mHomeView.setPresenter(this);
    }

    @Override
    public void fetchUserData() {
        mUsecaseHandler.execute(fetchUserData, null, new UseCase.UseCaseCallback<FetchUserData.ResponseValue>() {
            @Override
            public void onSuccess(FetchUserData.ResponseValue response) {
                mHomeView.showUserDetailsHeader(response.getUserDetails());
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    @Override
    public void fetchWalletBalance() {
        mHomeView.showWalletBalance(0);
    }
}
